/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bibliotecaextração;

/**
 *
 * @author alessandro
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author alessandromurtabaldi
 */
public class extractionLibraryOpenStreetMap {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String leitura = null;

        try {
            // TODO code application logic here
            leitura = readFile("file.osm"); //insert .osm exported from openstreetmap here

        } catch (IOException ex) {
            Logger.getLogger(extractionLibraryOpenStreetMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] leituras = leitura.split("<tag k=\"building\"");

        List<Double> coordlat = new ArrayList<Double>();
        List<Double> coordlon = new ArrayList<Double>();
        int totalex = 0;
        for (int cont = 0; cont < leituras.length - 1; cont++) {
            try{
            String atual[] = leituras[cont].split("<way id=");
            String ref[] = atual[atual.length - 1].split("nd ref=\"");
            Double latmedia = 0.0;
            Double lonmedia = 0.0;
            int total = 0;

            for (int cont2 = 1; cont2 < ref.length; cont2++) {
                String referencia = ref[cont2].substring(0, ref[cont2].indexOf(">") - 2);

                String link = "https://www.openstreetmap.org/api/0.6/node/" + referencia;

                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(link);
                String conteudo = null;
                try {
                    HttpResponse response = client.execute(request);
                    HttpEntity entity = response.getEntity();
                    conteudo = EntityUtils.toString(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (conteudo != null) {
                    totalex++;
                    System.out.println(totalex);
                    
                    String conteudomexer[] = conteudo.split("lon");
                    String latitude = conteudomexer[0].split("lat")[1];
                    latitude = latitude.substring(2, latitude.length() - 2);
                    String longitude = conteudomexer[1].substring(2, conteudomexer[1].indexOf("/") - 1);
                    double lat = Double.parseDouble(latitude);
                    double lon = Double.parseDouble(longitude);
                    latmedia = lat + latmedia;
                    lonmedia = lon + lonmedia;
                    total = total + 1;
                }
            }
            if(latmedia!=0.0&&lonmedia!=0.0){
            latmedia = latmedia / total;
            lonmedia = lonmedia / total;
            coordlat.add(latmedia);
            coordlon.add(lonmedia);
            }
            }catch(Exception e)
            {
                
            }
        }

        
        File arquivo = new File("points.txt"); //points of lat/lon of buildings to export
        try (FileWriter fw = new FileWriter(arquivo)) {
            fw.write("");
            double latmax;
            double latmin;
            double lonmax;
            double lonmin;
            latmax = coordlat.get(0);
            latmin = coordlat.get(0);
            lonmax = coordlon.get(0);
            lonmin = coordlon.get(0);
            for (int cont = 0; cont < coordlat.size(); cont++) {
                if (latmax < coordlat.get(cont)) {
                    latmax = coordlat.get(cont);
                }
                if (latmin > coordlat.get(cont)) {
                    latmin = coordlat.get(cont);
                }
                if (lonmax < coordlon.get(cont)) {
                    lonmax = coordlon.get(cont);
                }
                if (lonmin > coordlon.get(cont)) {
                    lonmin = coordlon.get(cont);
                }
            }

             
            for (int cont = 0; cont < coordlat.size(); cont++) {
                double cdlon=coordlon.get(cont);
                double cdlat=coordlat.get(cont);
                fw.write("lat:"+cdlat+",lon:"+cdlon);
                fw.write("\n");
            }
            fw.write("latmax:"+latmax+",latmin:"+latmin+",lonmax:"+lonmax+"lonmin:"+lonmin);
            fw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static String readFile(String fileName) throws IOException {//function to parse files
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
}
