package com.spencercjh;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.core.util.StringUtils;
import io.micronaut.validation.Validated;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.*;
import java.util.*;

import static com.spencercjh.TableHeader.*;

/**
 * @author spencercjh
 */
@Command(name = "divide-squad", description = "...",
  mixinStandardHelpOptions = true)
@Slf4j
@Validated
public class DivideSquadCommand implements Runnable {

  private static final int USUAL_INITIAL_CAPACITY = 10;
  private static final int DATA_SHEET_INDEX = 0;
  @Option(names = {"-v", "--verbose"}, description = "...")
  private boolean verbose;

  @Option(required = true, names = {"-f", "--file"}, description = "Path of the Excel file with Player data")
  private String filePath;

  @Option(required = true, names = {"-s", "--size"}, description = "Squad size, 7 or 8")
  @Max(value = 8)
  @Min(value = 7)
  private int squadSize;

  public static void main(String[] args) throws Exception {
    PicocliRunner.run(DivideSquadCommand.class, args);
  }

  @Override
  public void run() {
    // business logic here
    if (verbose) {
      System.out.println("Hi!");
    }
    final Workbook workbook = getWorkbook();
    final Sheet sheet = getSheet(workbook);
    final Set<Player> players = new HashSet<>();
    final PriorityQueue<Player> strikerQueue = new PriorityQueue<>(Comparator.comparingInt(Player::getStrikerStats));
    final PriorityQueue<Player> middleFieldQueue = new PriorityQueue<>(Comparator.comparingInt(Player::getMiddleFieldStats));
    final PriorityQueue<Player> sideQueue = new PriorityQueue<>(Comparator.comparingInt(Player::getSideStats));
    final PriorityQueue<Player> halfbackQueue = new PriorityQueue<>(Comparator.comparingInt(Player::getHalfbackStats));
    final PriorityQueue<Player> goalkeeperQueue = new PriorityQueue<>(Comparator.comparingInt(Player::getGoalkeeperStats));
    // skip header
    for (int i = 1; i < sheet.getLastRowNum(); i++) {
      final Player player = extractPlayer(sheet.getRow(i));
      players.add(player);
      if (player.getStrikerStats() > 0) {
        strikerQueue.offer(player);
      }
      if (player.getMiddleFieldStats() > 0) {
        middleFieldQueue.offer(player);
      }
      if (player.getSideStats() > 0) {
        sideQueue.offer(player);
      }
      if (player.getHalfbackStats() > 0) {
        halfbackQueue.offer(player);
      }
      if (player.getGoalkeeperStats() > 0) {
        goalkeeperQueue.offer(player);
      }
    }
    final List<Player> squadAlpha = new ArrayList<>(USUAL_INITIAL_CAPACITY);
    final List<Player> squadBeta = new ArrayList<>(USUAL_INITIAL_CAPACITY);
    while (!strikerQueue.isEmpty()) {
      squadAlpha.add(strikerQueue.poll());
      if (!strikerQueue.isEmpty()) {
        squadBeta.add(strikerQueue.poll());
      }
    }
  }

  private Player extractPlayer(Row row) {
    return Player.builder()
      .name(row.getCell(NAME.getIndex()).getStringCellValue())
      .strikerStats(turnHyphenToZero(row.getCell(STRIKER_STATS.getIndex()).getStringCellValue()))
      .middleFieldStats(turnHyphenToZero(row.getCell(MIDDLE_FIELD_STATS.getIndex()).getStringCellValue()))
      .sideStats(turnHyphenToZero(row.getCell(SIDE_STATS.getIndex()).getStringCellValue()))
      .halfbackStats(turnHyphenToZero(row.getCell(HALFBACK_STATS.getIndex()).getStringCellValue()))
      .goalkeeperStats(turnHyphenToZero(row.getCell(GOALKEEPER_STATS.getIndex()).getStringCellValue()))
      .build();
  }

  private int turnHyphenToZero(String numberStr) {
    return StringUtils.isEmpty(numberStr) || !StringUtils.isDigits(numberStr) ? 0 : Integer.parseInt(numberStr);
  }


  private Sheet getSheet(Workbook workbook) {
    try {
      return workbook.getSheetAt(DATA_SHEET_INDEX);
    } catch (Exception e) {
      log.error("Read sheet: {} failed", DATA_SHEET_INDEX, e);
      System.exit(1);
      return null;
    }
  }

  private Workbook getWorkbook() {
    InputStream excelStream;
    try {
      excelStream = new BufferedInputStream(new FileInputStream(filePath));
    } catch (FileNotFoundException e) {
      log.error("Can't found the file: {}", filePath, e);
      System.exit(1);
      return null;
    }
    Workbook workbook;
    try {
      workbook = new XSSFWorkbook(excelStream);
    } catch (IOException e) {
      log.error("Can't open the excel", e);
      System.exit(1);
      return null;
    }
    return workbook;
  }
}
