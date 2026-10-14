package com.cvmanager.models;

/**
 * Modelo POJO que representa la entidad 'cv' (cabecera del currículum vítae).
 */
public class CV {

    private int cvId;
    private int graduateId;
    private String title;
    private String professionalSummary;
    private String cvPdfUrl;
    private boolean published;
    private int viewsCount;

    public CV() {
    }

    public CV(int cvId, int graduateId, String title, String professionalSummary, String cvPdfUrl, boolean published, int viewsCount) {
        this.cvId = cvId;
        this.graduateId = graduateId;
        this.title = title;
        this.professionalSummary = professionalSummary;
        this.cvPdfUrl = cvPdfUrl;
        this.published = published;
        this.viewsCount = viewsCount;
    }

    public int getCvId() {
        return cvId;
    }

    public void setCvId(int cvId) {
        this.cvId = cvId;
    }

    public int getGraduateId() {
        return graduateId;
    }

    public void setGraduateId(int graduateId) {
        this.graduateId = graduateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfessionalSummary() {
        return professionalSummary;
    }

    public void setProfessionalSummary(String professionalSummary) {
        this.professionalSummary = professionalSummary;
    }

    public String getCvPdfUrl() {
        return cvPdfUrl;
    }

    public void setCvPdfUrl(String cvPdfUrl) {
        this.cvPdfUrl = cvPdfUrl;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    @Override
    public String toString() {
        return "CV{" +
                "cvId=" + cvId +
                ", graduateId=" + graduateId +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", viewsCount=" + viewsCount +
                '}';
    }
}
