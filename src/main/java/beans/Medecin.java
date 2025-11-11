package beans;

/**
 * Classe Medecin - Hérite de User
 * Représente un médecin de la clinique
 */
public class Medecin extends User {
    
    private int specialiteId;
    private String numeroOrdre;
    
    // pour affichage 
    private String nomSpecialite;
    private String descriptionSpecialite;
    
  
    public Medecin() {
        super();
        this.role = "MEDECIN";
    }
    
  
    public Medecin(String nom, String prenom, String email, String telephone,
                   int specialiteId, String numeroOrdre) {
        super(nom, prenom, email, telephone, "MEDECIN");
        this.specialiteId = specialiteId;
        this.numeroOrdre = numeroOrdre;
    }
    
    
    public int getSpecialiteId() {
        return specialiteId;
    }
    
    public String getNumeroOrdre() {
        return numeroOrdre;
    }
    
    public String getNomSpecialite() {
        return nomSpecialite;
    }
    
    public String getDescriptionSpecialite() {
        return descriptionSpecialite;
    }
    
    
    public void setSpecialiteId(int specialiteId) {
        this.specialiteId = specialiteId;
    }
    
    public void setNumeroOrdre(String numeroOrdre) {
        this.numeroOrdre = numeroOrdre;
    }
    
    public void setNomSpecialite(String nomSpecialite) {
        this.nomSpecialite = nomSpecialite;
    }
    
    public void setDescriptionSpecialite(String descriptionSpecialite) {
        this.descriptionSpecialite = descriptionSpecialite;
    }
    
    @Override
    public String toString() {
        return "Medecin{"  +
                ", codeUser='" + codeUser + '\'' +
                ", nom='Dr. " + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", specialite='" + nomSpecialite + '\'' +
                ", numeroOrdre='" + numeroOrdre + '\'' +
                '}';
    }
}