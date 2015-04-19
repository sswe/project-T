import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Draw {

    private static ArrayList<String> City = new ArrayList<String>();
    private static String map;
    private static ArrayList<Coordinates> stations = new ArrayList<Coordinates>();
    private static ArrayList<Coordinates> lines = new ArrayList<Coordinates>();
    private static int ticks;
    private static ArrayList<Train> trains = new ArrayList<Train>();
    private static LinkedList<Edge> edge = new LinkedList<Edge>();
    public static void main (String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        
        StdDraw.setCanvasSize(1300, 680);
        StdDraw.setXscale(-650, 650);
        StdDraw.setYscale(-340,340);
         map = "High Speed Europe Map.png";
         if (map.contains("Amtrak")) {
             for (int i = 0; i < SequenceGenerator.AMTRAK_STATIONS.length; i++) {
             City.add(SequenceGenerator.AMTRAK_STATIONS[i]);
             }    
             readCoordinates("Amtrak Coordinates.txt");
            readLines("Amtrak System Map Data.txt");
           // edge = Graph.read("Amtrak System Map Data.txt");
         }else {
             for (int i = 0; i < SequenceGenerator.EUROPE_STATIONS.length; i++) {
             City.add(SequenceGenerator.EUROPE_STATIONS[i]);
             }    
             readCoordinates("Europe Coordinates.txt");
             readLines("High Speed Europe Map Data.txt");
             //edge = Graph.read("High Speed Europe Map Data.txt");
         }
         if (args.length != 0) {
             if (args[0].equals("edit")) {
            findStations();            
            editStations();
            editLines();
            return;
             }
         }
        
        animation();
                        
    }
    
    public static void drawStation () {
        for (int i = 0; i < stations.size(); i++) {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            StdDraw.setPenRadius(.05);
            StdDraw.point(stations.get(i).getX(), stations.get(i).getY());
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(stations.get(i).getX(), stations.get(i).getY(), Integer.toString(i+1));
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(.1);
            StdDraw.text(stations.get(i).getX() + 70, stations.get(i).getY(), City.get(i));
        }
    }
    
    private static void readCoordinates (String newPoints) throws FileNotFoundException {
        Scanner std = new Scanner(new File (newPoints));
        std.useDelimiter(",");
        while (std.hasNextLine()) {
            String a = std.next();
            int index = City.indexOf(a);
            if (index != -1) {
                stations.add(index, new Coordinates(std.nextDouble(),std.nextDouble()));
            }    
            std.nextLine();
        }
        std.close();
    }
    
    private static void findStations () {
        while(true) { 
            StdDraw.picture(0, 0, map);
            StdDraw.text(600, -200, StdDraw.mouseX()+","+StdDraw.mouseY());  
            StdDraw.show(100);
            if (StdDraw.hasNextKeyTyped()) {
                break;
            }
        }
    }
    
    private static void editStations () throws FileNotFoundException {
        while (true) {
            drawBackground();
            for (int i = 0; i < stations.size(); i++) {
                if (StdDraw.mouseX() <= stations.get(i).getX() + 10 && StdDraw.mouseX() >= stations.get(i).getX() - 10
                        &&StdDraw.mouseY() <= stations.get(i).getY() + 10 && StdDraw.mouseY() >= stations.get(i).getY() - 10) {
                    while (StdDraw.mousePressed()) {
                        stations.set(i, new Coordinates (StdDraw.mouseX(),StdDraw.mouseY()));
                    }
                }
            }
            StdDraw.show(100);
            if (StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                break;
            }
        }
        PrintWriter stn = new PrintWriter("point.txt");
        for (int i = 0; i < City.size(); i++) {
            stn.print(City.get(i)+",");
            stn.print(stations.get(i).getX()+",");
            stn.print(stations.get(i).getY()+",");
            stn.println();
        }
        stn.close();
    }
    
    public static void drawBackground () {
        StdDraw.clear();
        //StdDraw.picture(0, 0, map);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.setPenRadius(.01);  
        drawLines();
        drawStation();
        
    }
    
    private static void readLines (String name) throws FileNotFoundException {
        Scanner std = new Scanner(new File (name));
        std.useDelimiter(",\\s*");
        while (std.hasNextLine()) {
            StdDraw.setPenColor(StdDraw.ORANGE);
            String a = std.next(), b=std.next();
            int first = City.indexOf(a);
            int second = City.indexOf(b);
            lines.add(stations.get(first));
            lines.add(stations.get(second));
            std.nextLine();
        }
        std.close();
    }
    
    private static void editLines () throws FileNotFoundException { 
        int i = 0, count = 0; 
        ArrayList<Coordinates> points = new ArrayList<Coordinates>();
        while (true) {            
            drawBackground();
            while (StdDraw.mousePressed()) {
                if (i==0) {
                    points.add(new Coordinates(StdDraw.mouseX(),StdDraw.mouseY()));
                    i++;
                }
            }
            if (i != 0) {
                points.add(new Coordinates(StdDraw.mouseX(),StdDraw.mouseY()));
                lines.add(points.get(count));
                count = points.size()-1;
                lines.add(points.get(count));            
                i = 0;
                if (StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                    break;
                }
            }
            StdDraw.show(1);
        }
        
        PrintWriter stn = new PrintWriter("Line.txt");
        for (int j = 0; j < lines.size(); j+=2) {
            stn.print(lines.get(j).getX()+",");
            stn.print(lines.get(j).getY()+",");
            stn.print(lines.get(j+1).getX()+",");
            stn.print(lines.get(j+1).getY()+",");
            stn.println();
        }
        stn.close();
    }

    private static void drawLines () {
        for (int i = 0; i < lines.size(); i+=2 ) {
            StdDraw.setPenRadius(.01);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.line(lines.get(i).getX(), lines.get(i).getY(),
                        lines.get(i+1).getX(), lines.get(i+1).getY());
            StdDraw.setPenRadius(.003);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.line(lines.get(i).getX(), lines.get(i).getY(),
                        lines.get(i+1).getX(), lines.get(i+1).getY());
        }
    }

    public static void drawTrain (Coordinates a) {
        StdDraw.setPenRadius(.01);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledRectangle(a.getX(), a.getY(), 10, 1);
        
        
    }
    
    public static void readSchedule (String filename) {
        int i = 0;
        Scanner stdinput = new Scanner(filename);
        LinkedList<Edge> edges = new LinkedList<>();
        stdinput.useDelimiter(",\\s*");
        while (stdinput.hasNextLine()) {
            int id = stdinput.nextInt();
            int speed = stdinput.nextInt();
            int time = stdinput.nextInt();
            String a = stdinput.next();
            if (a.contains("[")) {
                edges.add(new Edge(new Vertex(a.substring(1)), new Vertex(stdinput.next()), stdinput.nextInt()));
                while (stdinput.hasNext()) {
                    String b = stdinput.next();
                    String c = stdinput.next();
                    String d = stdinput.next();
                     if (d.contains("]")) {
                         edges.add(new Edge(new Vertex(b), new Vertex(c), Integer.parseInt(d.substring(1))));
                         return;
                     }else {
                         edges.add(new Edge(new Vertex(b), new Vertex(c), Integer.parseInt(d)));
                     }
                }               
            }
            trains.add(new Train(edges.get(0).getVertexOne().getID(),edges.get(edge.size() - 1).getVertexTwo().getID(),
                    time, id));            
            trains.get(i).setSpeed(speed);
            trains.get(i).setRoute(edges);
            i++;
        }
        stdinput.close();
    }
    
    public static int findIndex (String source,String destination) {
        
        for (int i = 0; i < edge.size(); i++) {
            if (edge.get(i).getVertexOne().getID().equals(source)) {
                if (edge.get(i).getVertexTwo().getID().equals(destination)) {
                    return i;
                }
            }else if (edge.get(i).getVertexOne().getID().equals(destination)) {
                if (edge.get(i).getVertexTwo().getID().equals(source)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void move (Train running) {
       Coordinates a = running.getCoordinates();
       Edge b = running.getCurrentEdge();
       Coordinates first = stations.get(City.indexOf(b.getVertexOne().getID()));
       Coordinates second = stations.get(City.indexOf(b.getVertexTwo().getID()));
       Coordinates ratio = second.subtract(first);
       double t = b.getWeight() / running.getSpeed();
       ratio.divide(t);
       a.add(ratio);
       running.setCoordinates(a);
       drawTrain(a);
    }
    
    public static void atStation (Coordinates stop) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setPenRadius(.05);
        StdDraw.point(stop.getX(), stop.getY());
    }
    
    public static void drawCurrent (Coordinates start, Coordinates end) {
        StdDraw.setPenRadius(.01);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.line(start.getX(), start.getY(),
                    end.getX(), end.getY());
        StdDraw.setPenRadius(.003);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.line(start.getX(), start.getY(),
                    end.getX(), end.getY());
    }
    
    public static void animation () {
        readSchedule("train.txt");
        int time = 0;
        ArrayList<Train> active = new ArrayList<Train>();
        while (true) {
            drawBackground();
            for (int i = 0; i < trains.size(); i++) {
                if (trains.get(i).getDepatureTime() == time) {
                    active.add(trains.get(0));
                }
            }
            for (int i = 0; i < active.size(); i++) {
                if (stations.indexOf(active.get(i).getCoordinates()) != -1) {
                    atStation(active.get(i).getCoordinates());
                    active.remove(i);
                }
                for (int j = 0; j < active.get(i).getRoute().size(); j++) {
                    drawCurrent(stations.get(City.indexOf(active.get(i).getRoute().get(j).getVertexOne().getID())),
                            stations.get(City.indexOf(active.get(i).getRoute().get(j).getVertexTwo().getID())));
                }
                
                move(active.get(i));                
                
            }
            StdDraw.show(100);
        }
    }
}
                
