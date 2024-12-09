package us.aldwin.ambient.consumer;

import org.junit.jupiter.api.Test;

public class ToRemoveJavaTest {
  @Test
  public void itReturnsTheExpectedInformation() {
    assert "Foo".equals(ToRemove.placeholder());
  }

  @SuppressWarnings("AccessStaticViaInstance")
  @Test
  public void itReturnsTheExpectedInformationUsingInstance() {
    assert "Foo".equals(ToRemove.INSTANCE.placeholder());
  }
}
