// Controles del portal; no envían cambios al servidor.
document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.getElementById('sidebar-toggle');
    if (toggle) {
        const screen = window.matchMedia('(max-width: 992px)');
        const sidebar = document.getElementById('portal-sidebar');
        function syncSidebar() {
            const opened = screen.matches ? document.body.classList.contains('sidebar-open') : !document.body.classList.contains('sidebar-collapsed');
            sidebar.inert = !opened;
            toggle.setAttribute('aria-expanded', String(opened));
        }
        toggle.addEventListener('click', () => {document.body.classList.toggle(screen.matches ? 'sidebar-open' : 'sidebar-collapsed');syncSidebar();});
        document.addEventListener('keydown', event => {if(event.key === 'Escape' && screen.matches){document.body.classList.remove('sidebar-open');syncSidebar();toggle.focus();}});
        screen.addEventListener('change', syncSidebar);syncSidebar();
    }
    document.querySelectorAll('.portal-sidebar nav a').forEach(link => {
        const url = new URL(link.href);
        if (url.pathname === location.pathname && url.search === location.search) {
            link.classList.add('active'); link.setAttribute('aria-current', 'page');
        }
    });
    document.querySelectorAll('[data-print]').forEach(button => button.addEventListener('click', () => {const url=new URL(location.href);url.searchParams.set('print','yes');location.assign(url);}));
    if(new URLSearchParams(location.search).get('print')==='yes')window.print();
    document.querySelectorAll('[data-local-table]').forEach(table => {
        new DataTable(table, {
            pageLength:10, lengthMenu:[10,25,50,100], order:[],
            columnDefs:[{targets:-1,orderable:false,searchable:false}],
            language:{search:'Buscar:',lengthMenu:'Mostrar _MENU_ registros',info:'Mostrando _START_ a _END_ de _TOTAL_ registros',infoEmpty:'Sin registros',infoFiltered:'(de _MAX_ registros)',zeroRecords:'No se encontraron coincidencias',emptyTable:'No hay registros disponibles',paginate:{first:'Primero',last:'Último',next:'Siguiente',previous:'Anterior'}}
        });
    });
});
