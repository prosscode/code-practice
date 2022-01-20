package code.flink.table.submit;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.data.RowData;
import org.apache.flink.types.Row;
import org.roaringbitmap.RoaringBitmap;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * @date 2022/1/18
 * @created by shuang.peng
 * @description Utils
 */
public class Utils {

    public static void RefreshKafkaOffset(Row offsetRow, Map<Tuple2<String,Integer>,Tuple2<Long,Long>> offsetMap){
        String topic = (String) offsetRow.getField(0);
        Integer partition = (Integer) offsetRow.getField(1);
        Long offset = (Long) offsetRow.getField(2);
        Tuple2<String,Integer> topicPart = new Tuple2<>(topic,partition);
        Tuple2<Long,Long> minMax = offsetMap.get(topicPart);
        if(minMax==null) minMax = new Tuple2<>(Long.MAX_VALUE,Long.MIN_VALUE);
        if(offset < minMax.f0 ) minMax.f0 = offset;
        if(offset > minMax.f1 ) minMax.f1 = offset;
        offsetMap.put(topicPart,minMax);
    }

    public static String Serialize(RoaringBitmap bitmap) {
        try {
            ByteArrayDataOutput output = ByteStreams.newDataOutput(bitmap.serializedSizeInBytes());
            bitmap.serialize(output);
            return Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RoaringBitmap Deserialize(String serializedBitmap) {
        byte[] rawBytes = java.util.Base64.getDecoder().decode(serializedBitmap);
        RoaringBitmap bitmap = new RoaringBitmap();
        try {
            bitmap.deserialize(ByteStreams.newDataInput(rawBytes));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Long VersionFrom(Row row, int rowIndex) {
        String rk = row.getField(rowIndex).toString();
        return VersionFrom(rk);
    }

    public static Long VersionFrom(RowData row, int rowIndex) {
        String rk = row.getString(rowIndex).toString();
        return VersionFrom(rk);
    }

    public static String IdFrom(String idTsVal){
        int[] versionBound = versionBound(idTsVal);
        return idTsVal.substring(0,versionBound[0]-1);
    }

    // row key is $id_$ts_$val or $id_$ts, id and val may contain '_'
    public static Long VersionFrom(String rowKey) {
        int[] versionBound = versionBound(rowKey);
        return Long.parseLong(rowKey.substring(versionBound[0], versionBound[1])) * 1000l;
    }

    private static int[] versionBound(String rowKey) {
        int leftPos, rightPos = rowKey.length();
        while (true) {
            leftPos = rowKey.lastIndexOf('_', rightPos - 1);
            if (leftPos == -1) throw new IllegalArgumentException("invalid row key:" + rowKey);
            if (validVersion(rowKey, leftPos + 1, rightPos)) {
                return new int[]{leftPos + 1, rightPos};
            }
            rightPos = leftPos;
        }
    }

    // ts string length is 10 and starts with '1'
    private static boolean validVersion(String rk, int from, int to) {
        if (to - from != 10) return false;
        if (rk.charAt(from) != '1') return false;
        for (int i = from; i < to; ++i) {
            char c = rk.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    public static void MergeKafkaOffset(Map<Tuple2<String, Integer>, Tuple2<Long, Long>> a, Map<Tuple2<String, Integer>, Tuple2<Long, Long>> b) {
        for(Map.Entry<Tuple2<String, Integer>, Tuple2<Long, Long>> entry : b.entrySet()){
            Tuple2<Long, Long> minMax = a.get(entry.getKey());
            if(minMax == null) minMax = entry.getValue();
            else {
                if(minMax.f0 > entry.getValue().f0)  minMax.f0 = entry.getValue().f0;
                if(minMax.f1 < entry.getValue().f1)  minMax.f1 = entry.getValue().f1;
            }
            a.put(entry.getKey(),minMax);
        }
    }

}
