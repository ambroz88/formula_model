package com.ambroz.formula.gamemodel.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ambroz.formula.gamemodel.datamodel.Point;
import com.ambroz.formula.gamemodel.datamodel.Polyline;
import com.ambroz.formula.gamemodel.enums.Side;
import com.ambroz.formula.gamemodel.track.Track;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public abstract class TrackIO {

    public static String[] getTracksArray() {
        String[] tracks = new String[]{};
        File directory = new File("tracks");

        if (directory.exists()) {

            File[] trackFiles = directory.listFiles();

            if (trackFiles != null) {
                tracks = new String[trackFiles.length];

                int i = 0;
                for (File track : trackFiles) {
                    tracks[i] = track.getName().substring(0, track.getName().lastIndexOf("."));
                    i++;
                }
            }
        }

        return tracks;
    }

    public static void trackToJSON(Track track, String name) throws IOException {
        JSONObject obj = new JSONObject();

        obj.put("width", track.getMaxWidth());
        obj.put("height", track.getMaxHeight());

        obj.put("left", transformTrackLine(track, Side.Left));

        obj.put("right", transformTrackLine(track, Side.Right));

        trackToFile(name, obj);
    }

    private static List<List<Integer>> transformTrackLine(Track track, Side side) {
        List<List<Integer>> pointList = new ArrayList<>();
        Polyline trackLine = track.getLine(side);

        for (int i = 0; i < trackLine.getLength(); i++) {
            pointList.add(new ArrayList<>(Arrays.asList(trackLine.getPoint(i).getX(), trackLine.getPoint(i).getY())));
        }
        return pointList;
    }

    private static void trackToFile(String name, JSONObject obj) throws JSONException, IOException {
        String filePath = getTrackFilePath(name + ".json");
        try {
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(obj.toString(4));
                file.close();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public static Track trackFromJSON(String name) {
        String filePath = getTrackFilePath(name + ".json");

        try {
            JSONObject jsonObject = new JSONObject(FileIO.readFileToString(filePath));

            // load left barrier
            Polyline left = loadTrackLine(jsonObject.getJSONArray("left"));

            // load right barrier
            Polyline right = loadTrackLine(jsonObject.getJSONArray("right"));

            // load game properties
            String width = jsonObject.get("width").toString();
            String height = jsonObject.get("height").toString();

            // load new track
            Track track = new Track();
            track.setLeft(left);
            track.setRight(right);

            track.setMaxWidth(Integer.valueOf(width));
            track.setMaxHeight(Integer.valueOf(height));
            return track;

        } catch (IOException | JSONException ex) {
            Logger.getLogger(Track.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static Polyline loadTrackLine(JSONArray pointArray) throws NumberFormatException {
        List<Object> coordinatesArray;
        String X;
        String Y;
        Polyline line = new Polyline();

        for (Object pointObject : pointArray.toList()) {
            coordinatesArray = (List) pointObject;
            X = coordinatesArray.get(0).toString();
            Y = coordinatesArray.get(1).toString();
            line.addPoint(new Point(Integer.valueOf(X), Integer.valueOf(Y)));
        }

        return line;
    }

    public static void deleteTrack(String name) {
        FileUtils.getFile(getTrackFilePath(name + ".json")).delete();
    }

    private static String getTrackFilePath(String name) {
        return "tracks/" + name;
    }

}
