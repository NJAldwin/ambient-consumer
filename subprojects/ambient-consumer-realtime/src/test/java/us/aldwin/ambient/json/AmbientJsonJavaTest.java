package us.aldwin.ambient.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AmbientJsonJavaTest {
  @Test
  public void itInstantiatesAMapper() throws JsonProcessingException {
    ObjectMapper mapper = AmbientJson.getMapper();

    String val = mapper.readValue("\"hello\"", String.class);
    assertEquals("hello", val);
  }
}
