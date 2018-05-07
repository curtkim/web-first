package examples;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

public class WordCountProcessor implements Processor<String, String> {
  private ProcessorContext context;
  private KeyValueStore<String, Long> kvStore;

  @SuppressWarnings("unchecked")
  public void init(final ProcessorContext context) {
    // keep the processor context locally because we need it in punctuate() and commit()
    this.context = context;
    // retrieve the key-value store named "Counts"
    this.kvStore = (KeyValueStore<String, Long>) context.getStateStore("Counts");

    // schedule a punctuation method every 1000 milliseconds.
    this.context.schedule(1000, PunctuationType.WALL_CLOCK_TIME, new Punctuator() {
      public void punctuate(long timestamp) {
        System.out.println("--");
        KeyValueIterator<String, Long> iter = kvStore.all();

        while (iter.hasNext()) {
          KeyValue<String, Long> entry = iter.next();
          System.out.println(entry);
          context.forward(entry.key, entry.value.longValue());
        }

        // it is the caller's responsibility to close the iterator on state store;
        // otherwise it may lead to memory and file handlers leak depending on the
        // underlying state store implementation.
        iter.close();

        // commit the current processing progress
        context.commit();
      }
    });

  }

  public void process(String dummy, String line) {
    System.out.println(line);
    String[] words = line.toLowerCase().split(" ");

    for (String word : words) {
      Long oldValue = this.kvStore.get(word);

      if (oldValue == null)
        kvStore.put(word, 1L);
      else
        kvStore.put(word, oldValue + 1L);
    }
  }

  public void punctuate(long timestamp) {
  }

  public void close() {
  }
};