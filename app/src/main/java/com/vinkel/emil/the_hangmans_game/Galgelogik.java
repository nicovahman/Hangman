package com.vinkel.emil.the_hangmans_game;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Galgelogik {
    /**
     * AHT afprøvning er muligeOrd synlig på pakkeniveau
     */
    ArrayList<String> muligeOrd = new ArrayList<String>();
    private String ordet;
    private ArrayList<String> brugteBogstaver = new ArrayList<String>();
    private ArrayList<String> forkerteBogstaver = new ArrayList<String>();
    private String synligtOrd;
    private int antalForkerteBogstaver;
    private boolean sidsteBogstavVarKorrekt;
    private boolean spilletErVundet;
    private boolean spilletErTabt;


    public ArrayList<String> getBrugteBogstaver() {
        return brugteBogstaver;
    }

    public String getSynligtOrd() {
        return synligtOrd;
    }

    public String getOrdet() {
        return ordet;
    }

    public int getAntalForkerteBogstaver() {
        return antalForkerteBogstaver;
    }

    public boolean erSidsteBogstavKorrekt() {
        return sidsteBogstavVarKorrekt;
    }

    public boolean erSpilletVundet() {
        return spilletErVundet;
    }

    public boolean erSpilletTabt() {
        return spilletErTabt;
    }

    public ArrayList<String> getForkertebogstaver() {
        return forkerteBogstaver;
    }

    public boolean erSpilletSlut() {
        return spilletErTabt || spilletErVundet;
    }


    public Galgelogik() {

    }

    //tilføjer mulige ord til arrayet fra text fil ud fra en valgt kategori. @author emil_
    public void categoriesAndDifficulty(Enum cat, Enum difficulty, Context context) {
        try {
            InputStream is = null;
            if (MyEnum.DEFAULT.equals(cat)) {
                is = (context.getAssets().open("def.txt"));

            } else if (MyEnum.STARWARS.equals(cat)) {
                is = (context.getAssets().open("starwars.txt"));

            } else if (MyEnum.HARRYPOTTER.equals(cat)) {
                is = (context.getAssets().open("harrypotter.txt"));

            } else if (MyEnum.FOOD.equals(cat)) {
                is = (context.getAssets().open("food.txt"));

            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();

            while (line != null) {
                String[] wordsLine = line.split(",");
                for (String word : wordsLine) {
                    muligeOrd.add(word);
                }
                line = reader.readLine();
            }

            if (difficulty == MyEnum.EASY) {
                setWords(MyEnum.EASY.getIntvalue());
            }
            if (difficulty == MyEnum.NORMAL) {
                setWords(MyEnum.NORMAL.getIntvalue());
            }


            nulstil();


        } catch (Exception e) {
            throw new RuntimeException(
                    "This should never happen, I know this file exists", e);
        }
    }


    private void setWords(int i) {
        ArrayList<String> temp = new ArrayList<String>();
        for (String word : muligeOrd) {
            if (word.length() <= i) {
                temp.add(word);

            }
        }
        muligeOrd.retainAll(temp);

        if (muligeOrd.isEmpty()) {
            muligeOrd.add("ingenord");
        }

    }

    public void nulstil() {
        brugteBogstaver.clear();
        forkerteBogstaver.clear();
        antalForkerteBogstaver = 0;
        spilletErVundet = false;
        spilletErTabt = false;
        ordet = muligeOrd.get(new Random().nextInt(muligeOrd.size()));
        opdaterSynligtOrd();
    }


    private void opdaterSynligtOrd() {
        synligtOrd = "";
        spilletErVundet = true;
        brugteBogstaver.add("-");

        for (int n = 0; n < ordet.length(); n++) {
            String bogstav = ordet.substring(n, n + 1);

            if (brugteBogstaver.contains(bogstav)) {
                synligtOrd = synligtOrd + bogstav;
            } else {
                synligtOrd = synligtOrd + " _ ";
                spilletErVundet = false;
            }
        }
    }

    public void gætBogstav(String bogstav) {
        if (bogstav.length() != 1) return;

        if (brugteBogstaver.contains(bogstav)) return;
        if (spilletErVundet || spilletErTabt) return;

        brugteBogstaver.add(bogstav);

        if (ordet.contains(bogstav)) {
            sidsteBogstavVarKorrekt = true;

        } else {
            // Vi gættede på et bogstav der ikke var i ordet.
            sidsteBogstavVarKorrekt = false;
            antalForkerteBogstaver = antalForkerteBogstaver + 1;
            forkerteBogstaver.add(bogstav);
            if (antalForkerteBogstaver > 6) {
                spilletErTabt = true;
            }
        }
        opdaterSynligtOrd();
    }

    public void logStatus() {
        System.out.println("---------- ");
        System.out.println("- ordet (skult) = " + ordet);
        System.out.println("- synligtOrd = " + synligtOrd);
        System.out.println("- forkerteBogstaver = " + antalForkerteBogstaver);
        System.out.println("- brugeBogstaver = " + brugteBogstaver);
        if (spilletErTabt) System.out.println("- SPILLET ER TABT");
        if (spilletErVundet) System.out.println("- SPILLET ER VUNDET");
        System.out.println("---------- ");
    }


    public static String hentUrl(String url) throws IOException {
        System.out.println("Henter data fra " + url);
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        StringBuilder sb = new StringBuilder();
        String linje = br.readLine();
        while (linje != null) {
            sb.append(linje + "\n");
            linje = br.readLine();
        }
        return sb.toString();
    }


    public void hentOrdFraDr() throws Exception {
        String data = hentUrl("https://dr.dk");
        //System.out.println("data = " + data);

        data = data.substring(data.indexOf("<body")). // fjern headere
                replaceAll("<.+?>", " ").toLowerCase(). // fjern tags
                replaceAll("&#198;", "æ"). // erstat HTML-tegn
                replaceAll("&#230;", "æ"). // erstat HTML-tegn
                replaceAll("&#216;", "ø"). // erstat HTML-tegn
                replaceAll("&#248;", "ø"). // erstat HTML-tegn
                replaceAll("&oslash;", "ø"). // erstat HTML-tegn
                replaceAll("&#229;", "å"). // erstat HTML-tegn
                replaceAll("[^a-zæøå]", " "). // fjern tegn der ikke er bogstaver
                replaceAll(" [a-zæøå] ", " "). // fjern 1-bogstavsord
                replaceAll(" [a-zæøå][a-zæøå] ", " "); // fjern 2-bogstavsord

        System.out.println("data = " + data);
        System.out.println("data = " + Arrays.asList(data.split("\\s+")));
        muligeOrd.clear();
        muligeOrd.addAll(new HashSet<String>(Arrays.asList(data.split(" "))));

        System.out.println("muligeOrd = " + muligeOrd);
        nulstil();
    }
}
