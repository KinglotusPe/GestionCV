package com.cvmanager.services;
import com.cvmanager.utils.ValidacionUtil;
/** Prepara el texto de recuperación para entrega manual por el administrador.
 * No envía correos automáticamente ni requiere credenciales SMTP.
 */
public class EmailServicio {
 public String recoveryDraft(String recipient,String link) {
  if(!ValidacionUtil.email(recipient))throw new IllegalArgumentException("Correo inválido.");
  return "Destinatario: "+recipient+"\nAsunto: Recuperación de acceso a CVManager\n\nAbra este enlace para elegir una contraseña nueva: "+link+"\nCaduca en 30 minutos y solo se puede usar una vez.";
 }
}
