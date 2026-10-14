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
