package call;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CallSummaryProcessor implements Processor<Long, Call> {
  private ProcessorContext context;
  private Map<String, CallSummary> map = new HashMap<>();
  private Function<Call, String> toRegion;

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

  public CallSummaryProcessor(Function<Call, String> toRegion){
    this.toRegion = toRegion;
  }

  @SuppressWarnings("unchecked")
  public void init(final ProcessorContext context) {
    this.context = context;

    this.context.schedule(60*1000, PunctuationType.STREAM_TIME, new Punctuator() {
      public void punctuate(long timestamp) {
        String time = dateFormat.format(new Date(timestamp));

        for(String region : map.keySet()) {
          System.out.println("forward " + time + ":" + region);
          context.forward(time + ":" + region, map.get(region));
        }
        context.commit();

        map = new HashMap<>();
      }
    });
  }


  @Override
  public void process(Long id, Call value) {
    String region = toRegion.apply(value);
    CallSummary prev = map.containsKey(region) ? map.get(region) : new CallSummary();
    map.put(region, prev.add(value));
  }

  @Override
  public void punctuate(long timestamp) {}  // deprecated; not used

  @Override
  public void close() {

  }
}

