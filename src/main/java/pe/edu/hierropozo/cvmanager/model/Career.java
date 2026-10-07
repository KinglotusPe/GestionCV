package pe.edu.hierropozo.cvmanager.model;

/**
 * Modelo POJO que representa la entidad 'carreras' (programas de estudio).
 */
public class Career {

    private int careerId;
    private String name;
    private String code;
    private String description;
    private int durationYears;
    private boolean active;

    public Career() {
    }

    public Career(int careerId, String name, String code, String description, int durationYears, boolean active) {
        this.careerId = careerId;
        this.name = name;
        this.code = code;
        this.description = description;
        this.durationYears = durationYears;
        this.active = active;
    }

    public int getCareerId() {
        return careerId;
    }

    public void setCareerId(int careerId) {
        this.careerId = careerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(int durationYears) {
        this.durationYears = durationYears;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Career{" +
                "careerId=" + careerId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", active=" + active +
                '}';
    }
}
