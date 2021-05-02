package com.spencercjh.service;

import io.micronaut.core.io.ResourceLoader;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@MicronautTest
class PoiServiceTest {
  @Inject
  private ResourceLoader resourceLoader;

  @Inject
  private PoiService poiService;

  @Test
  void readWorkbook() throws IOException {
    assertNotNull(poiService.readWorkbook(resourceLoader.getResource("test.xlsx").get().getPath()));
    assertThrows(FileNotFoundException.class, () -> poiService.readSheet("not-exist.xlsx"));
  }

  @Test
  void readSheet() throws IOException {
    assertNotNull(poiService.readSheet(resourceLoader.getResource("test.xlsx").get().getPath()));
    assertThrows(IllegalArgumentException.class, () -> poiService.readSheet(resourceLoader.getResource("test.xlsx").get().getPath(), -1));
    assertThrows(IllegalArgumentException.class, () -> poiService.readSheet(resourceLoader.getResource("test.xlsx").get().getPath(), 3));

    assertNotNull(poiService.readSheet(resourceLoader.getResource("test_with_first_data_sheet.xlsx").get().getPath(), 1));
  }
}
