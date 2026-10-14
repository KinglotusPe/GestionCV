package com.cvmanager.models;
/** Resultado de una revisión confirmada; nunca representa un guardado pendiente. */
public record CompanyReview(int companyId,String companyName,String decision,String reason,String reviewedAt) {
 public String statusLabel(){return switch(decision){case "active"->"Activa";case "suspended"->"Suspendida";default->"Inactiva / rechazada";};}
}
