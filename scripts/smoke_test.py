"""HTTP regression checks. Run only against the isolated local test deployment.
Usage: python scripts/smoke_test.py --isolated --base http://127.0.0.1:18081/cvmanager
Requires target/test-config.json with setupToken. Never use a production database.
"""
import argparse, base64, html, http.cookiejar, json, re, secrets, struct, urllib.request, urllib.parse, urllib.error, zlib
from pathlib import Path

parser=argparse.ArgumentParser();parser.add_argument('--isolated',action='store_true');parser.add_argument('--base',default='http://127.0.0.1:18081/cvmanager');args=parser.parse_args()
assert args.isolated and urllib.parse.urlparse(args.base).hostname in ('127.0.0.1','localhost'), 'An isolated localhost deployment is required'
BASE=args.base.rstrip('/');config_path=Path('target/test-config.json');config=json.loads(config_path.read_text())
checks=[]
def check(label,condition):
    assert condition,label
    checks.append(label);print('PASS',label,flush=True)
class Client:
    def __init__(self):
        self.cookies=http.cookiejar.CookieJar();self.opener=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(self.cookies))
    def request(self,path,data=None,headers=None):
        url=path if path.startswith('http') else BASE+path
        if isinstance(data,dict):data=urllib.parse.urlencode(data).encode()
        try:r=self.opener.open(urllib.request.Request(url,data=data,headers=headers or {}),timeout=30)
        except urllib.error.HTTPError as e:r=e
        body=r.read();self.last=(r.code,body.decode('utf-8',errors='replace'),r.url)
        if r.code==500:Path('target/smoke-error.html').write_text(self.last[1],encoding='utf-8')
        return self.last
    def token(self,page):
        status,text,url=self.request(page);assert status==200,(page,status,url)
        match=re.search(r'name="csrfToken" value="([^"]+)"',text);assert match,('Missing CSRF',page)
        return match.group(1)
    def post(self,path,data,page=None):
        data=dict(data);data['csrfToken']=self.token(page or path)
        return self.request(path,data)
    def login(self,email,password):return self.post('/login',dict(email=email,password=password))
    def upload(self,kind,content,name,mime):
        fields={'csrfToken':self.token('/graduate/cv'),'type':kind};boundary='----test'+secrets.token_hex(12);parts=[]
        for k,v in fields.items():parts.append(f'--{boundary}\r\nContent-Disposition: form-data; name="{k}"\r\n\r\n{v}\r\n'.encode())
        parts.append(f'--{boundary}\r\nContent-Disposition: form-data; name="file"; filename="{name}"\r\nContent-Type: {mime}\r\n\r\n'.encode()+content+b'\r\n');parts.append(f'--{boundary}--\r\n'.encode())
        return self.request('/graduate/upload',b''.join(parts),{'Content-Type':'multipart/form-data; boundary='+boundary})

admin=Client();guest=Client();password='TestPassword!'+secrets.token_hex(8);suffix=secrets.token_hex(5)
if 'adminEmail' not in config:
    config.update(adminEmail='http-admin-'+suffix+'@example.com',adminPassword=password);config_path.write_text(json.dumps(config))
    result=admin.post('/setup-admin',dict(email=config['adminEmail'],password=password,setupToken=config['setupToken']))
    check('initial administrator setup',result[0]==200 and 'adminCreated=1' in result[2])
check('admin login',admin.login(config['adminEmail'],config['adminPassword'])[2].endswith('/admin/dashboard'))
check('anonymous private route redirects', '/login?auth=1' in guest.request('/graduate/cv')[2])
check('CSRF rejected',admin.request('/admin/users',{'id':'1','status':'active'})[0]==403)
users={}
for role,label in [('graduate','grad'),('graduate','other'),('company','company')]:
    email=f'http-{label}-{suffix}@example.com';client=Client();result=client.post('/register',dict(email=email,password=password,role=role,firstName='María',lastName='Pruebas',companyName='Empresa HTTP'))
    check(label+' registration',result[0]==200 and 'Registro completado' in result[1])
    page=admin.request('/admin/users')[1];row=next(r for r in re.findall(r'<tr\b[^>]*>(.*?)</tr>',page,re.S) if email in r);uid=re.search(r'#(\d+)',row).group(1)
    check(label+' activation',admin.post('/admin/users',dict(id=uid,newRole=role,status='active',action='update'))[0]==200)
    check(label+' login',client.login(email,password)[0]==200)
    users[label]=(client,email,uid)
grad,email,gid=users['grad'];other,_,oid=users['other'];company,_,cid=users['company']
check('role boundary',grad.request('/admin/users')[0]==403)
profile=dict(firstName='María " onfocus="alert(1)',lastName='Pruebas',documentType='DNI',documentNumber='',phone='999123456',address='',city='Ayacucho',country='Perú',birthDate='',linkedinUrl='',portfolioUrl='',careerId='1',graduationYear='2026',publicProfile='on')
check('graduate profile saves',grad.post('/graduate/profile',profile)[0]==200)
check('second blank document saves',other.post('/graduate/profile',{**profile,'firstName':'Otro'})[0]==200)
check('invalid DNI rejected',grad.post('/graduate/profile',{**profile,'documentNumber':'bad'})[0]==400)
check('CV main saves',grad.post('/graduate/cv',dict(action='main',title='Técnica de redes '+suffix,summary='Experiencia profesional'))[0]==200)
sections=[('Education','education',{'institution':'Instituto','degree':'Técnico','fieldOfStudy':'Redes','startDate':'2020-01-01','endDate':'2023-01-01','description':'Formación'}),('Experience','experience',{'companyName':'Empresa','position':'Técnico','startDate':'2024-01-01','endDate':'2025-01-01','responsibilities':'Soporte'}),('Skill','skill',{'skillName':'Java','category':'technical','proficiencyLevel':'3'}),('Language','language',{'languageName':'Inglés','proficiencyLevel':'intermediate'}),('Certification','certification',{'name':'Redes','organization':'Instituto','issueDate':'2025-01-01'}),('Reference','reference',{'fullName':'Referencia','position':'Jefe','companyName':'Empresa','phone':'999123456','email':'ref@example.com'})]
for action,kind,fields in sections:
    result=grad.post('/graduate/cv',dict(action='add'+action,**fields));check('add '+kind,result[0]==200)
    match=re.search(r'action=edit&amp;type='+kind+r'&amp;id=(\d+)',result[1]);assert match,kind
    item=match.group(1);edit_path='/graduate/cv?action=edit&type='+kind+'&id='+item
    check('edit form '+kind,grad.request(edit_path)[0]==200)
    check('save edit '+kind,grad.post('/graduate/cv',dict(action='update',type=kind,id=item,**fields),edit_path)[0]==200)
    check('ownership '+kind,other.post('/graduate/cv',dict(action='update',type=kind,id=item,**fields))[0]==400)
check('publish CV',grad.post('/graduate/cv',dict(action='publish'))[0]==200)
result=company.request('/company/search?q='+urllib.parse.quote('Técnica de redes '+suffix)+'&year=2026');check('search and graduation filter',result[0]==200 and 'Técnica de redes' in result[1])
cv=re.search(r'/company/cv\?id=(\d+)',result[1]).group(1)
page=company.request('/company/cv?id='+cv);check('company CV renders',page[0]==200)
check('stored attribute is escaped','placeholder="Estimado/a María " onfocus=' not in page[1] and 'onfocus=&#034;' in page[1])
check('favorite',company.post('/company/action',dict(action='favorite',cvId=cv),'/company/cv?id='+cv)[0]==200)
check('favorite list',company.request('/company/favorites')[0]==200)
check('contact request',company.post('/company/action',dict(action='contact',graduateId=re.search(r'name="graduateId" value="(\d+)"',page[1]).group(1),message='Entrevista de prueba'),'/company/cv?id='+cv)[0]==200)
# User ID and graduate ID are different; resolve graduateId from actual form if needed.
page=grad.request('/graduate/requests');check('graduate inbox',page[0]==200 and 'Entrevista de prueba' in page[1])
request_id=re.search(r'name="id" value="(\d+)"',page[1]).group(1)
check('contact private before acceptance',email not in company.request('/company/favorites')[1])
check('accept contact',grad.post('/graduate/requests',dict(id=request_id,status='accepted'))[0]==200)
check('contact released after acceptance',email in company.request('/company/favorites')[1])
pdf=b'%PDF-1.4\n1 0 obj\n<< /Type /Catalog >>\nendobj\ntrailer\n<< /Root 1 0 R >>\n%%EOF\n'
check('PDF upload',grad.upload('pdf',pdf,'cv.pdf','application/pdf')[0]==200)
check('PDF download with consent',company.request('/files?type=pdf&cvId='+cv)[0]==200)
def chunk(kind,data):return struct.pack('!I',len(data))+kind+data+struct.pack('!I',zlib.crc32(kind+data)&0xffffffff)
png=b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('!2I5B',2,2,8,2,0,0,0))+chunk(b'IDAT',zlib.compress(b'\x00'+b'\x00\x80\x40'*2+b'\x00'+b'\x00\x80\x40'*2))+chunk(b'IEND',b'')
check('photo upload',grad.upload('photo',png,'photo.png','image/png')[0]==200)
check('photo display',company.request('/files?type=photo&cvId='+cv)[0]==200)
check('fake PDF rejected',grad.upload('pdf',b'<script>fake</script>','fake.pdf','application/pdf')[0]==400)
check('revoke contact',grad.post('/graduate/requests',dict(id=request_id,status='rejected'))[0]==200)
check('PDF access revoked',company.request('/files?type=pdf&cvId='+cv)[0]==404)
check('company profile',company.post('/company/profile',dict(companyName='Empresa HTTP',ruc='',industry='Tecnología',companySize='10',phone='999123456',website='https://example.com',description='Empresa de pruebas'))[0]==200)
job=dict(action='save',title='Vacante HTTP '+suffix,description='Soporte técnico',city='Ayacucho',salary='A convenir',modality='onsite',status='open',careerId='1',closingDate='')
result=company.post('/company/jobs',job);check('create vacancy',result[0]==200 and job['title'] in result[1])
job_id=re.search(r'\?edit=(\d+)',result[1]).group(1)
check('edit vacancy form',company.request('/company/jobs?edit='+job_id)[0]==200)
check('apply with consent',grad.post('/graduate/jobs',dict(action='apply',id=job_id,message='Mi postulación',consent='yes'))[0]==200)
result=company.request('/company/jobs');check('company sees applicant',result[0]==200 and email in result[1])
app_id=re.search(r'name="action" value="respond"><input type="hidden" name="id" value="(\d+)"',result[1]).group(1)
check('evaluate applicant',company.post('/company/jobs',dict(action='respond',id=app_id,status='reviewing'))[0]==200)
check('hide public CV',grad.post('/graduate/cv',dict(action='unpublish'))[0]==200)
check('application grants private CV',company.request('/company/cv?id='+cv)[0]==200)
check('application grants PDF',company.request('/files?type=pdf&cvId='+cv)[0]==200)
check('withdraw application',grad.post('/graduate/jobs',dict(action='respond',id=app_id,status='withdrawn'))[0]==200)
check('withdraw revokes private CV',company.request('/company/cv?id='+cv)[0]==404)
check('withdraw revokes PDF',company.request('/files?type=pdf&cvId='+cv)[0]==404)
check('close vacancy',company.post('/company/jobs',{**job,'id':job_id,'status':'closed'})[0]==200)
check('closed vacancy rejects applications',other.post('/graduate/jobs',dict(action='apply',id=job_id,message='Prueba',consent='yes'))[0]==400)
check('remove PDF',grad.post('/graduate/upload',dict(type='pdf',action='remove'),'/graduate/cv')[0]==200)
check('removed PDF unavailable',grad.request('/files?type=pdf&cvId='+cv)[0]==404)
check('remove photo',grad.post('/graduate/upload',dict(type='photo',action='remove'),'/graduate/profile')[0]==200)
check('removed photo unavailable',grad.request('/files?type=photo&cvId='+cv)[0]==404)
page=grad.request('/graduate/cv')[1]
for action,kind,fields in sections:
    item=re.search(r'action=edit&amp;type='+kind+r'&amp;id=(\d+)',page).group(1)
    check('delete '+kind,grad.post('/graduate/cv',dict(action='delete',type=kind,id=item))[0]==200)
# Extended institutional labor portal, in the same isolated fixtures.
from datetime import date, timedelta
import io, zipfile, xml.etree.ElementTree as ET
future=(date.today()+timedelta(days=2)).isoformat()
past=(date.today()-timedelta(days=2)).isoformat()
check('future vacancy saves',company.post('/company/jobs',{**job,'id':job_id,'openingDate':future,'status':'open'})[0]==200)
check('future vacancy blocks application',other.post('/graduate/jobs',dict(action='apply',id=job_id,message='Prueba futura',consent='yes'))[0]==400)
check('inverted dates rejected',company.post('/company/jobs',{**job,'id':job_id,'openingDate':future,'closingDate':past})[0]==400)
check('unsafe external link rejected',company.post('/company/jobs',{**job,'id':job_id,'externalUrl':'javascript:alert(1)'})[0]==400)
check('admin cannot edit company owned vacancy',admin.request('/admin/jobs?edit='+job_id)[0]==400)
check('graduate cannot access labor reports',grad.request('/admin/reports')[0]==403)
check('reopen vacancy',company.post('/company/jobs',{**job,'id':job_id,'openingDate':'','closingDate':''})[0]==200)
check('vacancy detail page',grad.request('/graduate/jobs?view='+job_id)[0]==200)
check('missing document rejected',grad.request('/graduate/jobs?document=2147483647')[0]==404)
fields={'csrfToken':company.token('/company/jobs'),'action':'upload','id':job_id,'title':'Bases de prueba'}
boundary='----labor'+secrets.token_hex(12)
parts=[f'--{boundary}\r\nContent-Disposition: form-data; name="{k}"\r\n\r\n{v}\r\n'.encode() for k,v in fields.items()]
parts.append(f'--{boundary}\r\nContent-Disposition: form-data; name="file"; filename="bases.pdf"\r\nContent-Type: application/pdf\r\n\r\n'.encode()+pdf+b'\r\n')
parts.append(f'--{boundary}--\r\n'.encode())
page=company.request('/company/jobs',b''.join(parts),{'Content-Type':'multipart/form-data; boundary='+boundary})
check('vacancy document upload',page[0]==200 and 'Bases de prueba' in page[1])
doc=re.search(r'\?document=(\d+)',page[1]).group(1)
check('graduate downloads vacancy document',grad.request('/graduate/jobs?document='+doc)[0]==200)
check('archive company vacancy',admin.post('/admin/jobs',dict(action='archive',id=job_id,archived='yes'))[0]==200)
check('archived document unavailable to graduate',grad.request('/graduate/jobs?document='+doc)[0]==404)
check('archived vacancy blocks application',other.post('/graduate/jobs',dict(action='apply',id=job_id,message='Prueba archivo',consent='yes'))[0]==400)
check('archive list',admin.request('/admin/jobs?archive=yes')[0]==200)
check('restore vacancy',admin.post('/admin/jobs',dict(action='archive',id=job_id,archived='no'))[0]==200)
check('delete vacancy document',company.post('/company/jobs',dict(action='removeDocument',id=job_id,documentId=doc))[0]==200)
check('removed vacancy document unavailable',grad.request('/graduate/jobs?document='+doc)[0]==404)
for report in ['jobs','companies','careers','applicants']:
    path='/admin/reports?kind='+report+'&start=2000-01-01&end=2099-12-31'
    check(report+' report renders',admin.request(path)[0]==200)
    response=admin.opener.open(BASE+path+'&format=xlsx');content=response.read()
    with zipfile.ZipFile(io.BytesIO(content)) as book:
        ET.fromstring(book.read('xl/worksheets/sheet1.xml'))
    check(report+' XLSX export',response.code==200 and content.startswith(b'PK'))
check('reports invalid date rejected',admin.request('/admin/reports?start=2026-12-31&end=2026-01-01')[0]==400)
check('reports empty range renders',admin.request('/admin/reports?start=1900-01-01&end=1900-01-02')[0]==200)
page=admin.request('/admin/companies')
check('company review page',page[0]==200)
row=next(r for r in re.findall(r'<tr\b[^>]*>(.*?)</tr>',page[1],re.S) if users['company'][1] in r)
company_id=re.search(r'name="id" value="(\d+)"',row).group(1)
check('company review requires reason',admin.post('/admin/companies',dict(id=company_id,decision='active',reason=''))[0]==400)
check('company review records reason',admin.post('/admin/companies',dict(id=company_id,decision='active',reason='Documentación revisada '+suffix))[0]==200)
ajax_headers={'Accept':'application/json'}
ajax_data=dict(id=company_id,decision='active',reason='Revisión Ajax "completa" <script>texto</script> '+suffix,csrfToken=admin.token('/admin/companies'))
response=admin.request('/admin/companies',ajax_data,ajax_headers)
check('Ajax transaction returns JSON',response[0]==200 and json.loads(response[1])['reason']==ajax_data['reason'])
check('Ajax history escapes HTML','&lt;script&gt;texto&lt;/script&gt;' in admin.request('/admin/companies')[1])
response=admin.request('/admin/companies',{**ajax_data,'reason':''},ajax_headers)
check('Ajax validation returns 400 JSON',response[0]==400 and bool(json.loads(response[1])['message']))
response=admin.request('/admin/companies',{**ajax_data,'csrfToken':''},ajax_headers)
check('Ajax CSRF returns 403 JSON',response[0]==403 and bool(json.loads(response[1])['message']))
response=guest.request('/admin/companies',ajax_data,ajax_headers)
check('Ajax anonymous returns 401 JSON',response[0]==401 and bool(json.loads(response[1])['message']))
response=grad.request('/admin/companies',{**ajax_data,'csrfToken':grad.token('/graduate/cv')},ajax_headers)
check('Ajax role boundary returns 403 JSON',response[0]==403 and bool(json.loads(response[1])['message']))
check('DataTables local asset',admin.request('/assets/vendor/datatables/dataTables.min.js')[0]==200)
report_html=admin.request('/admin/reports?kind=jobs&start=2000-01-01&end=2099-12-31')[1]
check('DisplayTag sortable report',bool(re.search(r'd-\d+-s=',report_html)))
check('institutional vacancy creation',admin.post('/admin/jobs',{**job,'title':'Convocatoria institucional '+suffix,'companyId':company_id})[0]==200)

check('audit page',admin.request('/admin/audit')[0]==200)
check('career page',admin.request('/admin/careers')[0]==200)
career=dict(action='create',name='Carrera HTTP '+suffix,code='T'+suffix,description='Prueba',durationYears='3',active='on')
page=admin.post('/admin/careers',career);check('create career',page[0]==200 and career['name'] in page[1])
row=next(r for r in re.findall(r'<tr\b[^>]*>(.*?)</tr>',page[1],re.S) if career['name'] in r)
career_id=re.search(r'name="id" value="(\d+)"',row).group(1)
check('edit career',admin.post('/admin/careers',{**career,'action':'update','id':career_id,'description':'Actualizada'})[0]==200)
check('deactivate career',admin.post('/admin/careers',dict(action='delete',id=career_id))[0]==200)
check('reactivate career',admin.post('/admin/careers',{**career,'action':'update','id':career_id})[0]==200)
page=admin.request('/admin/users')[1];row=next(r for r in re.findall(r'<tr\b[^>]*>(.*?)</tr>',page,re.S) if config['adminEmail'] in r);admin_id=re.search(r'#(\d+)',row).group(1)
check('protect current administrator',admin.post('/admin/users',dict(id=admin_id,newRole='graduate',status='active',firstName='Admin',lastName='Prueba',action='update'))[0]==400)
check('suspend account',admin.post('/admin/users',dict(id=cid,newRole='company',status='suspended',action='update'))[0]==200)
check('existing session revoked','/login?auth=1' in company.request('/company/search')[2])
result=admin.post('/admin/users',dict(id=oid,action='resetPassword'));check('issue reset link',result[0]==200)
reset_path=html.unescape(re.search(r'href="([^"]+/reset-password\?token=[a-f0-9]+)"',result[1]).group(1));reset_path=reset_path.removeprefix('/cvmanager')
check('reset password',guest.post('/reset-password',dict(token=urllib.parse.parse_qs(urllib.parse.urlparse(reset_path).query)['token'][0],password='ChangedPassword!123',confirmPassword='ChangedPassword!123'),reset_path)[0]==200)
check('old session revoked after reset','/login?auth=1' in other.request('/graduate/profile')[2])
check('reset single use',guest.post('/reset-password',dict(token=urllib.parse.parse_qs(urllib.parse.urlparse(reset_path).query)['token'][0],password='ChangedPassword!123',confirmPassword='ChangedPassword!123'),reset_path)[0]==400)
check('login after reset',other.login(users['other'][1],'ChangedPassword!123')[0]==200)
check('change role creates company profile',admin.post('/admin/users',dict(id=oid,newRole='company',status='active',companyName='Empresa convertida',action='update'))[0]==200)
check('role change revokes session','/login?auth=1' in other.request('/graduate/profile')[2])
check('new company can sign in',other.login(users['other'][1],'ChangedPassword!123')[2].endswith('/company/search'))
check('new company profile renders',other.request('/company/profile')[0]==200)
check('account password change',other.post('/account',dict(currentPassword='ChangedPassword!123',password='FinalPassword!123',confirmPassword='FinalPassword!123'))[0]==200)
check('logout',admin.post('/logout',{},'/admin/dashboard')[0]==200)
check('logout clears authentication','/login?auth=1' in admin.request('/admin/dashboard')[2])
Path('target/http-test-results.json').write_text(json.dumps({'passed':len(checks),'checks':checks},ensure_ascii=False,indent=2),encoding='utf-8')
print('HTTP checks passed:',len(checks))
