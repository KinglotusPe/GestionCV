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
