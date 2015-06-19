
package JDOM2;

import java.io.*;
import java.util.Date;
import org.jdom2.*;
import org.jdom2.input.*;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

class Joueur {
    int id;
    String nom;
    int rang;
    int nbr_tanks_detruits;
    int nbr_tanks_perdus;
    
    Joueur(int id, String nom, int rang, int nbrDetruits, int nbrPerdus) {
        this.id = id;
        this.nom = nom;
        this.rang = rang;
        this.nbr_tanks_detruits = nbrDetruits;
        this.nbr_tanks_perdus = nbrPerdus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }

    public int getRang() {
        return rang;
    }

    public int getNbr_tanks_detruits() {
        return nbr_tanks_detruits;
    }

    public int getNbr_tanks_perdus() {
        return nbr_tanks_perdus;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public void setNbr_tanks_detruits(int nbr_tanks_detruits) {
        this.nbr_tanks_detruits = nbr_tanks_detruits;
    }

    public void setNbr_tanks_perdus(int nbr_tanks_perdus) {
        this.nbr_tanks_perdus = nbr_tanks_perdus;
    }
    
    @Override
    public String toString() {
        return id + ".- " + nom + " rang : " + rang + " Détruits : " + nbr_tanks_detruits + " Perdus : " + nbr_tanks_perdus;
    }
}

class Score {
    LinkedList<Joueur> joueurs = new LinkedList<>();
    Date date;
    
    void addJoueur(Joueur j) {
        joueurs.add(j);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public LinkedList<Joueur> getJoueurs() {
        return joueurs;
    }
    
    @Override
    public String toString() {
        String result = "";
        
        result += "Score de la partie du " + date + "\n";
        for(Joueur j : joueurs)
            result += "\t" + j + "\n";
        return result;
    }
}

public class JDOM2
{
   static org.jdom2.Document document;
   static Element racine;
   
   static void storeScore(Score score) throws JDOMException {
       racine = new Element("Scores");
       document = new Document(racine);
       
       Element elementScore = new Element("score");
       racine.addContent(elementScore);
       
       Element datePartie = new Element("date_partie");
       LinkedList<Attribute> dateAttribut = new LinkedList<>();
       dateAttribut.add(new Attribute("annee", String.valueOf(score.getDate().getYear())));
       dateAttribut.add(new Attribute("mois", String.valueOf(score.getDate().getMonth())));
       dateAttribut.add(new Attribute("jour", String.valueOf(score.getDate().getDate())));
       datePartie.setAttributes(dateAttribut);
       elementScore.addContent(datePartie);
       
       Element nbreJoueurs = new Element("nombre_joueurs");
       nbreJoueurs.setText(String.valueOf(score.joueurs.size()));
       elementScore.addContent(nbreJoueurs);
       
       LinkedList<Element> joueurs = new LinkedList<>();
       Attribute idJoueur = new Attribute("idPlayer", null);
       Element elementJoueur = new Element("joueur");
       Element nomJoueur = new Element("nom");
       Element rangJoueur = new Element("rang");
       Element nbrTankDetruits = new Element("nbr_tanks_detruits");
       Element nbrTankPerdus = new Element("nbr_tanks_perdus");
       
       for(Joueur j : score.getJoueurs()) {
           idJoueur.setValue(String.valueOf(j.getId()));
           elementJoueur.setAttribute(idJoueur);
           nomJoueur.setText(j.getNom());
           elementJoueur.addContent(nomJoueur);
           rangJoueur.setText(String.valueOf(j.getRang()));
           elementJoueur.addContent(rangJoueur);
           nbrTankDetruits.setText(String.valueOf(j.getNbr_tanks_detruits()));
           elementJoueur.addContent(nbrTankDetruits);
           nbrTankPerdus.setText(String.valueOf(j.getNbr_tanks_perdus()));
           elementJoueur.addContent(nbrTankPerdus);
           joueurs.add(elementJoueur);
       }
       elementScore.addContent(joueurs);
       
   }
   static void getScores() throws JDOMException {
       SAXBuilder sxb = new SAXBuilder();
       File file = new File("scores.xml");
       LinkedList<Score> scores = new LinkedList<>();
       boolean esFichier = file.isFile();
       if(!file.canRead()) {
           System.out.println("Le fichier n'a pas pu être ouvert.\n");
       }
       
       try {
           document = sxb.build(file);
       } catch (IOException ex) {
           Logger.getLogger(JDOM2.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       racine = document.getRootElement();
       
       List listScores = racine.getChildren("score");
       
       Iterator iter = listScores.iterator();
       
       while(iter.hasNext()) {
           Score score = new Score();
           
           // On récupère un score
           Element scoreCourant = (Element)iter.next();
           
           // On récupère la date de la partie
           Element date_partie = scoreCourant.getChild("date_partie");
           
           score.setDate(new Date(Integer.parseInt(date_partie.getAttributeValue("annee")),
                                  Integer.parseInt(date_partie.getAttributeValue("mois")),
                                  Integer.parseInt(date_partie.getAttributeValue("jour"))));
           
           // On recupère le nombre de joueurs
           Element nombreJoueur = scoreCourant.getChild("nombre_joueurs");
           
           // On récupère la liste des joueurs
           List listJoueurs = scoreCourant.getChildren("joueur");
           
           Iterator iterJoueur = listJoueurs.iterator();
           
           while(iterJoueur.hasNext()) {
               Element joueurCourant = (Element)iterJoueur.next();
               Element nomJoueur = joueurCourant.getChild("nom");
               Element rang = joueurCourant.getChild("rang");
               Element nbr_tanks_detruits = joueurCourant.getChild("nbr_tanks_detruits");
               Element nbr_tanks_perdus = joueurCourant.getChild("nbr_tanks_perdus");
               Attribute idJoueur = joueurCourant.getAttribute("idPlayer");
               
               Joueur joueur = new Joueur(Integer.parseInt(idJoueur.getValue().replaceAll(" ", "")),
                                          nomJoueur.getText().replaceAll(" ", ""), 
                                          Integer.parseInt(rang.getText().replaceAll(" ", "")), 
                                          Integer.parseInt(nbr_tanks_detruits.getText().replaceAll(" ", "")), 
                                          Integer.parseInt(nbr_tanks_perdus.getText().replaceAll(" ", "")));
               
               score.addJoueur(joueur);
           }
           
           scores.add(score);
           
       }
       
       // return listScores;
       scores.stream().forEach(((s)-> {
           System.out.println(s + "\n");
       }));
   }
   
   public static void main(String[] args)
   {
       try {
           getScores();
           
       } catch (JDOMException ex) {
           Logger.getLogger(JDOM2.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}