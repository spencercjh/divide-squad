package com.spencercjh;

import com.spencercjh.model.Player;
import com.spencercjh.model.SquadListResult;
import com.spencercjh.model.SquadSetResult;
import com.spencercjh.service.PoiService;
import com.spencercjh.service.SquadService;
import io.micronaut.configuration.picocli.PicocliRunner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Properties;


/**
 * @author spencercjh
 */
@Command(name = "divide-squad",
  description = "For more detail: https://github.com/spencercjh/divide-squad. Please prepare the excel file before dividing.",
  mixinStandardHelpOptions = true,
  versionProvider = DivideSquadCommand.PropertiesVersionProvider.class,
  header = {
    "@|green  _____   ___  _____     ___ _____  ___   _   _   ___  _____  |@",
    "@|green /  __ \\ / _ \\|_   _|   |_  |_   _|/ _ \\ | | | | / _ \\|  _  | |@",
    "@|green | /  \\// /_\\ \\ | |       | | | | / /_\\ \\| |_| |/ /_\\ \\ | | | |@",
    "@|green | |    |  _  | | |       | | | | |  _  ||  _  ||  _  | | | | |@",
    "@|green | \\__/\\| | | |_| |_  /\\__/ /_| |_| | | || | | || | | \\ \\_/ / |@",
    "@|green  \\____/\\_| |_/\\___/  \\____/ \\___/\\_| |_/\\_| |_/\\_| |_/\\___/ |@"})
@Slf4j
public class DivideSquadCommand implements Runnable {

  @Inject
  private PoiService poiService;
  @Inject
  private SquadService squadService;

  @SuppressWarnings("unused")
  @Option(required = true, names = {"-f", "--file"}, description = "Path of the Excel file with Player data")
  private String filePath;

  @SuppressWarnings("unused")
  @Option(names = {"-s", "--sheet"}, description = "Sheet Index (begin from 0, default value is 0)", defaultValue = "0")
  private Integer sheetIndex;

  @SuppressWarnings("unused")
  @Option(names = {"-l", "--loss"}, description = "Balance the difference in stats between the two teams. NOTICE, It's an experimental feature.")
  private boolean needLossCompensation;

  public static void main(String[] args) {
    PicocliRunner.run(DivideSquadCommand.class, args);
  }

  @SneakyThrows
  @Override
  public void run() {

    final List<Player> allPlayers = poiService.getAllPlayers(filePath, sheetIndex);
    final List<List<Player>> playersInDifferentPositions = squadService.groupPlayersByPositions(allPlayers);
    final SquadSetResult squadSetResult = squadService.divideSquad(allPlayers, playersInDifferentPositions,needLossCompensation);
    output(squadSetResult.sortPlayersInSquad());
  }

  private void output(SquadListResult listResult) {
    listResult.toMap().forEach((name, squad) -> {
      final double sum = squad.stream().mapToDouble(Player::getOverallStats).sum();
      System.out.printf("%s Squad: %d players, total stats: %d, average stats: %f%n", name, squad.size(), ((int) sum),
        sum / squad.size());
      squad.forEach(player -> System.out.println(player.getName() + " " + player.getOverallStats()));
    });
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
        return new String[]{"No version.txt file found in the classpath."};
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
