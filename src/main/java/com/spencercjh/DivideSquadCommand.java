package com.spencercjh;

import com.spencercjh.algorithm.DivideAlgorithm;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.validation.Validated;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.util.*;

import static com.spencercjh.TableHeader.*;

/**
 * @author spencercjh
 */
@Command(name = "divide-squad",
  description = "divide squad cli app",
  mixinStandardHelpOptions = true,
  versionProvider = DivideSquadCommand.PropertiesVersionProvider.class)
@Slf4j
@Validated
public class DivideSquadCommand implements Runnable {
  private static final int DATA_SHEET_INDEX = 0;
  @Inject
  private DivideAlgorithm divideAlgorithm;
  @SuppressWarnings("unused")
  @Option(required = true, names = {"-f", "--file"}, description = "Path of the Excel file with Player data")
  private String filePath;

  public static void main(String[] args) throws Exception {
    PicocliRunner.run(DivideSquadCommand.class, args);
  }

  @Override
  public void run() {
    final Workbook workbook = getWorkbook();
    final Sheet sheet = getSheet(workbook);
    final List<Player> strikers = new ArrayList<>();
    final List<Player> middleFields = new ArrayList<>();
    final List<Player> sides = new ArrayList<>();
    final List<Player> halfbacks = new ArrayList<>();
    final List<Player> goalkeepers = new ArrayList<>();
    final Set<Player> alpha = new HashSet<>();
    final Set<Player> bravo = new HashSet<>();
    // skip header
    for (int i = 1; i < sheet.getLastRowNum(); i++) {
      final Player player = extractPlayer(sheet.getRow(i));
      if (player.getStrikerStats() > 0) {
        strikers.add(player);
      }
      if (player.getMiddleFieldStats() > 0) {
        middleFields.add(player);
      }
      if (player.getSideStats() > 0) {
        sides.add(player);
      }
      if (player.getHalfbackStats() > 0) {
        halfbacks.add(player);
      }
      if (player.getGoalkeeperStats() > 0) {
        goalkeepers.add(player);
      }
    }
    final List<List<Player>> dividedStrikers = divideAlgorithm.divide(strikers, Position.STRIKER);
    boolean result = alpha.addAll(dividedStrikers.get(0)) && bravo.addAll(dividedStrikers.get(1));
    if (!result) {
      log.error("Failed when divide strikers");
      System.exit(1);
    }

    final List<List<Player>> dividedMiddleFields = divideAlgorithm.divide(middleFields, Position.MIDDLE_FIELD);
    result = alpha.addAll(dividedMiddleFields.get(0)) && bravo.addAll(dividedMiddleFields.get(1));
    if (!result) {
      log.error("Failed when divide middleFields");
      System.exit(1);
    }

    final List<List<Player>> dividedSides = divideAlgorithm.divide(sides, Position.SIDE);
    result = alpha.addAll(dividedSides.get(0)) && bravo.addAll(dividedSides.get(1));
    if (!result) {
      log.error("Failed when divide sides");
      System.exit(1);
    }

    final List<List<Player>> dividedHalfbacks = divideAlgorithm.divide(halfbacks, Position.HALFBACK);
    result = alpha.addAll(dividedHalfbacks.get(0)) && bravo.addAll(dividedHalfbacks.get(1));
    if (!result) {
      log.error("Failed when divide halfbacks");
      System.exit(1);
    }

    final List<List<Player>> dividedGoalkeepers = divideAlgorithm.divide(goalkeepers, Position.GOALKEEPER);
    result = alpha.addAll(dividedGoalkeepers.get(0)) && bravo.addAll(dividedGoalkeepers.get(1));
    if (!result) {
      log.error("Failed when divide goalkeepers");
      System.exit(1);
    }

    // TODO: Multi Position DeDuplication

    System.out.println("Alpha:");
    alpha.forEach(player -> System.out.println(player.getName() + " " + player.getOverallStats()));
    System.out.println("Alpha team total stats: " + alpha.stream().mapToDouble(Player::getOverallStats).sum());
    System.out.println("Bravo: ");
    bravo.forEach(player -> System.out.println(player.getName() + " " + player.getOverallStats()));
    System.out.println("Bravo team total stats: " + bravo.stream().mapToDouble(Player::getOverallStats).sum());
  }

  private Player extractPlayer(Row row) {
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

  /**
   * reference: https://github.com/remkop/picocli/blob/master/picocli-examples/src/main/java/picocli/examples/VersionProviderDemo2.java
   * <p>
   * {@link CommandLine.IVersionProvider} implementation that returns version information from a {@code /version.txt} file in the classpath.
   */
  @SuppressWarnings("unused")
  static class PropertiesVersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
      URL url = getClass().getResource("/version.txt");
      if (url == null) {
        return new String[]{"No version.txt file found in the classpath. Is examples.jar in the classpath?"};
      }
      Properties properties = new Properties();
      properties.load(url.openStream());
      return new String[]{
        properties.getProperty("Application-name") + " version \"" + properties.getProperty("Version") + "\"",
        "Built: " + properties.getProperty("Buildtime"),
      };
    }
  }
}
