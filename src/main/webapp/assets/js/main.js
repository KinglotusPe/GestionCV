/**
 * Scripts de interactividad y comportamiento de formularios.
 * CVManager Hierro Pozo.
 */
$(document).ready(function () {

    // Confirmación previa en formularios de acciones críticas
    $('.confirm-action').on('submit', function (event) {
        if (!confirm('¿Está seguro de que desea confirmar esta acción?')) {
            event.preventDefault();
        }
    });

    // Alternar campos condicionales en el registro según el rol seleccionado
    $('#role').on('change', function () {
        const isGraduate = $(this).val() === 'graduate';
        $('.graduate-fields').toggle(isGraduate);
        $('.company-fields').toggle(!isGraduate);
    }).trigger('change');

    // Validación asíncrona (Ajax) de disponibilidad de correo en registro
    $('#registerEmail').on('blur', function () {
        const email = $(this).val().trim();
        const feedback = $('#emailFeedback');

        if (!email) {
            feedback.text('').removeClass('text-success text-danger');
            return;
        }

        const contextPath = $('body').data('context') || '';

        $.ajax({
            url: contextPath + '/api/check-email',
            type: 'GET',
            data: { email: email },
            dataType: 'json',
            success: function (response) {
                if (!response.valid) {
                    feedback.text('Formato de correo no válido.').removeClass('text-success').addClass('text-danger');
                } else if (!response.available) {
                    feedback.text('Este correo ya se encuentra registrado.').removeClass('text-success').addClass('text-danger');
                } else {
                    feedback.text('✓ Correo disponible para registro.').removeClass('text-danger').addClass('text-success');
                }
            },
            error: function () {
                feedback.text('').removeClass('text-success text-danger');
            }
        });
    });

});

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

// jQuery + Bootstrap: validar en cliente sin sustituir la validación del servicio.
$(document).on('submit', '.ajax-review', function(event) {
    event.preventDefault();
    const form=this, $form=$(form), $feedback=$form.find('.ajax-feedback'), $button=$form.find('button[type="submit"],button:not([type])');
    $form.addClass('was-validated');
    if(!form.checkValidity()){form.reportValidity();return;}
    if($form.data('pending'))return;
    $form.data('pending',true);$button.prop('disabled',true);$feedback.removeClass('text-danger text-success').text('Guardando decisión…');
    $.ajax({url:form.action,method:'POST',data:$form.serialize(),dataType:'json',headers:{'Accept':'application/json'},timeout:20000})
      .done(result=>{
        const $row=$form.closest('tr');
        $row.find('[data-company-status]').text(result.statusLabel);
        // Actualizar la caché de búsqueda/ordenamiento de DataTables después del cambio.
        const table=$row.closest('table')[0];if(table && DataTable.isDataTable(table))new DataTable.Api(table).row($row[0]).invalidate('dom');
        $feedback.addClass('text-success').text(result.message);
        const $entry=$('<article>').addClass('border-bottom py-2');
        $('<strong>').text(result.companyName).appendTo($entry);
        $('<span>').text(' · '+result.statusLabel+' · '+result.reviewedAt).appendTo($entry);
        $('<p>').addClass('mb-1').text(result.reason).appendTo($entry);
        $('#review-history h2').after($entry);
      })
      .fail(xhr=>{$feedback.addClass('text-danger').text(xhr.responseJSON?.message || 'No se pudo confirmar el guardado. Recargue y revise el historial antes de reintentar.');})
      .always(()=>{$form.data('pending',false);$button.prop('disabled',false);});
});
