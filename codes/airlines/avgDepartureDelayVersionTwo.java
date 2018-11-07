import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class avgDepartureDelayVersionTwo {
        public static class Map extends Mapper<LongWritable, Text, Text, DelayAveragingPair> {
                private Text outText = new Text();
                private DelayAveragingPair pair = new DelayAveragingPair();

                public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
                        String line = value.toString();
                        String[] columnValues = line.split(",");

                        if (columnValues[15].compareTo("DepDelay") != 0 && columnValues[15].compareTo("NA") != 0){
                                outText.set(columnValues[8]);
                                int delayTime = Integer.parseInt(columnValues[15]);
                                pair.set(delayTime, 1);
                                context.write(outText, pair);
                        }
                }
        }

        public static class Combine extends Reducer<Text,DelayAveragingPair,Text,DelayAveragingPair> {
            private DelayAveragingPair pair = new DelayAveragingPair();

            @Override
            protected void reduce(Text key, Iterable<DelayAveragingPair> values, Context context) throws IOException, InterruptedException {
                        int delay = 0;
                        int count = 0;
                        for (DelayAveragingPair value : values) {
                        delay += value.getDelay().get();
                        count += value.getCount().get();
                        }
                        pair.set(delay,count);
                        context.write(key,pair);
            }
        }

        public static class Reduce extends Reducer<Text, DelayAveragingPair, Text, IntWritable> {
                public void reduce(Text key, Iterable<DelayAveragingPair> values, Context context)
                                throws IOException, InterruptedException {
                        int sum = 0;
                        int count = 0;
                        for (DelayAveragingPair val : values) {
                                sum += val.getDelay().get();
                                count += val.getCount().get();
                        }
                        context.write(key, new IntWritable(sum/count));
                }
        }

        public static class DelayAveragingPair implements Writable, WritableComparable<DelayAveragingPair> {
            private IntWritable delay;
            private IntWritable count;

        public DelayAveragingPair() {
                    set(new IntWritable(0), new IntWritable(0));
        }

                public DelayAveragingPair(int delay, int count) {
                    set(new IntWritable(delay), new IntWritable(count));
            }

                public void set(int delay, int count){
                        this.delay.set(delay);
                        this.count.set(count);
                }

                public void set(IntWritable delay, IntWritable count) {
                        this.delay = delay;
                        this.count = count;
                }

                @Override
                public void write(DataOutput out) throws IOException {
                        delay.write(out);
                        count.write(out);
                }

                @Override
                public void readFields(DataInput in) throws IOException {
                        delay.readFields(in);
                        count.readFields(in);
                }

                @Override
                public int compareTo(DelayAveragingPair other) {
                        int compareVal = this.delay.compareTo(other.getDelay());
                        if (compareVal != 0) {
                            return compareVal;
                        }
                        return this.count.compareTo(other.getCount());
                }

                public static DelayAveragingPair read(DataInput in) throws IOException {
                        DelayAveragingPair averagingPair = new DelayAveragingPair();
                        averagingPair.readFields(in);
                        return averagingPair;
                }

                @Override
                public boolean equals(Object o) {
                        if (this == o) return true;
                        if (o == null || getClass() != o.getClass()) return false;
                        DelayAveragingPair that = (DelayAveragingPair) o;

                        if (!count.equals(that.count)) return false;
                        if (!delay.equals(that.delay)) return false;

                        return true;
                    }

                @Override
                    public int hashCode() {
                        int result = delay.hashCode();
                        result = 163 * result + count.hashCode();
                        return result;
                }

                @Override
                public String toString() {
                        return "DelayAveragingPair{" +
                                "delay=" + delay +
                                ", count=" + count +
                                '}';
                }

                public IntWritable getDelay() {
                        return delay;
                }

                public IntWritable getCount() {
                        return count;
                }
        }

        public static void main(String[] args) throws Exception {
                Configuration conf = new Configuration();

                Job job = new Job(conf, "flight count");
                job.setJarByClass(avgDepartureDelayVersionTwo.class);

                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(DelayAveragingPair.class);

                job.setMapperClass(Map.class);
                job.setReducerClass(Reduce.class);
                job.setCombinerClass(Combine.class);

                job.setInputFormatClass(TextInputFormat.class);
                job.setOutputFormatClass(TextOutputFormat.class);

                FileInputFormat.addInputPath(job, new Path(args[0]));
                FileOutputFormat.setOutputPath(job, new Path(args[1]));

                job.waitForCompletion(true);
        }
}
