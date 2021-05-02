package com.spencercjh.service;

import com.spencercjh.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.inject.Singleton;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spencercjh.service.PoiService.TableHeader.*;

/**
 * @author spencercjh
 */
@Singleton
@Slf4j
public class PoiService {
  private static final int DEFAULT_DATA_SHEET_INDEX = 0;

  Workbook readWorkbook(String filePath) throws IOException {
    InputStream excelStream;
    try {
      excelStream = new BufferedInputStream(new FileInputStream(filePath));
    } catch (FileNotFoundException e) {
      log.error("Can't found the file: {}", filePath, e);
      throw e;
    }
    Workbook workbook;
    try {
      workbook = new XSSFWorkbook(excelStream);
    } catch (IOException e) {
      log.error("Can't open the excel", e);
      throw e;
    }
    return workbook;
  }

  Sheet readSheet(String filePath) throws IOException {
    return readSheet(filePath, DEFAULT_DATA_SHEET_INDEX);
  }

  Sheet readSheet(String filePath, int index) throws IOException {
    if (index < 0) {
      log.error("Illegal sheet index: {} in {}", index, filePath);
      throw new IllegalArgumentException(String.format("Illegal sheet index: %d", index));
    }
    try {
      return readWorkbook(filePath).getSheetAt(index);
    } catch (Exception e) {
      log.error("Read sheet: {} failed", index, e);
      throw e;
    }
  }

  Player extractPlayerFromRow(Row row) {
    try {
      final int strikerStats = (int) row.getCell(STRIKER_STATS.getIndex()).getNumericCellValue();
      final int middleFieldStats = (int) row.getCell(MIDDLE_FIELD_STATS.getIndex()).getNumericCellValue();
      final int sideStats = (int) row.getCell(SIDE_STATS.getIndex()).getNumericCellValue();
      final int halfbackStats = (int) row.getCell(HALFBACK_STATS.getIndex()).getNumericCellValue();
      final int goalkeeperStats = (int) row.getCell(GOALKEEPER_STATS.getIndex()).getNumericCellValue();
      final int[] stats = new int[]{strikerStats, middleFieldStats, sideStats, halfbackStats, goalkeeperStats};
      return Player.builder()
        .name(row.getCell(NAME.getIndex()).getStringCellValue())
        .strikerStats(strikerStats)
        .middleFieldStats(middleFieldStats)
        .sideStats(sideStats)
        .halfbackStats(halfbackStats)
        .goalkeeperStats(goalkeeperStats)
        .overallStats(((double) Arrays.stream(stats).sum()) / Arrays.stream(stats).filter(x -> x != 0).count())
        .build();
    } catch (Exception e) {
      log.error("Read rows from sheet failed", e);
      throw e;
    }
  }

  public List<Player> getAllPlayers(String filePath, int sheetIndex) throws IOException {
    final Sheet sheet = readSheet(filePath, sheetIndex);
    final List<Player> allPlayers = new ArrayList<>();
    // skip header
    for (int i = 1; i < sheet.getLastRowNum(); i++) {
      final Player player = extractPlayerFromRow(sheet.getRow(i));
      log.debug("Reading row {}i : {}", i, player);
      allPlayers.add(player);
    }
    return allPlayers;
  }

  @Getter
  @AllArgsConstructor
  enum TableHeader {
    /**
     * 姓名
     */
    NAME(0),
    /**
     * 前锋
     */
    STRIKER_STATS(1),
    /**
     * 中场
     */
    MIDDLE_FIELD_STATS(2),
    /**
     * 边路
     */
    SIDE_STATS(3),
    /**
     * 中卫
     */
    HALFBACK_STATS(4),
    /**
     * 门将
     */
    GOALKEEPER_STATS(5);

    /**
     * 下标
     */
    private final int index;
  }
}
