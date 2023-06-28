import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      System.out.println("===================================");
      System.out.println("=         BATTLESHIP GAME         =");
      System.out.println("===================================");
      System.out.println();
      System.out.print("Masukkan Nama Player 1: ");
      String player1Name = scanner.nextLine();
      Player player1 = new Player(player1Name);

      System.out.print("Masukkan Nama Player 2: ");
      String player2Name = scanner.nextLine();
      Player player2 = new Player(player2Name);
      System.out.println();

      Battleship battleship = new Battleship(player1, player2);
      battleship.start();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
class Battleship implements Game {
  private Player player1;
  private Player player2;
  private Player currentPlayer;
  private boolean gameOver;

  public Battleship(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
    currentPlayer = player1;
    gameOver = false;
  }

  @Override
  public void start() {
    System.out.println("Ayo kita berperang");
    System.out.println("Player 1: " + player1.getName());
    System.out.println("Player 2: " + player2.getName());
    System.out.println("==========================================");
    System.out.println("Perang Akan Segera Dimulai, Semua Bersiap!");
    System.out.println("==========================================");

    initializeShips(player1);
    initializeShips(player2);

    while (!gameOver) {
      if (player1.getShips().isEmpty()) {
        gameOver = true;
        System.out.println("Pertempuran Berakhir! " + player2.getName() + " Memenankan Pertempuran... Mantappp");
      } else if (player2.getShips().isEmpty()) {
        gameOver = true;
        System.out.println("Pertempuran Berakhir! " + player1.getName() + " Memenankan Pertempuran... Mantappp");
      } else {
        playTurn(currentPlayer);
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
      }
    }
  }

  private void initializeShips(Player player) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Player " + player.getName() + ", Masukkan Koordinat Kapalmu.");
    for (int i = 0; i < 3; i++) {
      int x, y;
      do {
        System.out.print("Masukkan Koordinat Kapal x kamu " + (i + 1) + ": ");
        x = scanner.nextInt();
        System.out.print("Masukkan Koordinat Kapal y kamu " + (i + 1) + ": ");
        y = scanner.nextInt();
        System.out.println();
        if (x >= 1 && x <= 7 && y >= 1 && y <= 7) {
          break;
        } else {
          System.out.println("Koordinat tidak valid, masukkan angka antara 1-7.");
        }
      } while (true);

      Ship ship = new Shippp(new Coordinate(x - 1, y - 1));
      player.addShip(ship);
    }
  }

  @Override
  public void playTurn(Player player) {
    Scanner scanner = new Scanner(System.in);
    System.out.println(player.getName() + ", Sekarang Saatnya Giliranmu, Ayo Serang!!!");

    boolean validCoordinates = false;
    Coordinate coordinate = null;

    while (!validCoordinates) {
      int x = getCoordinateInput(scanner, "x");
      int y = getCoordinateInput(scanner, "y");
      coordinate = new Coordinate(x, y);

      if (checkCoordinate(player1, coordinate) || checkCoordinate(player2, coordinate)) {
        System.out.println("Koordinat Ini Telah Diserang. Masukkan koordinat baru.");
      } else {
        validCoordinates = true;
      }
    }

    handleShot(player, coordinate);
  }

  private int getCoordinateInput(Scanner scanner, String axis) {
    int coordinate;
    do {
      System.out.print("Masukkan Koordinat " + axis + ": ");
      coordinate = scanner.nextInt();

      if (coordinate >= 1 && coordinate <= 7) {
        break;
      } else {
        System.out.println("Koordinat Diluar Area Pertempuran. Masukkan koordinat antara 1-7.");
      }
    } while (true);

    return coordinate - 1;
  }


  private boolean checkCoordinate(Player player, Coordinate coordinate) {
    for (Coordinate c : player.getShotsFired()) {
      if (c.getX() == coordinate.getX() && c.getY() == coordinate.getY()) {
        return true;
      }
    }
    return false;
  }

  private void handleShot(Player player, Coordinate coordinate) {
    player.addShotFired(coordinate);
    boolean hit = false;
    Player opponentPlayer = (player == player1) ? player2 : player1;

    clearScreen();

    for (Ship ship : opponentPlayer.getShips()) {
      if (ship.isHit(coordinate)) {
        hit = true;
        if (opponentPlayer.getShips().size() == 1) {
          opponentPlayer.getShips().remove(ship);
          System.out.println("Kapal Target Telah Hancur");
        } else {
          opponentPlayer.getShips().remove(ship);
          System.out.println("Target Berhasil Diserang!");
        }
        break;
      }
    }

    if (!hit) {
      for (Ship ship : player.getShips()) {
        if (ship.isHit(coordinate)) {
          hit = true;
          if (player.getShips().size() == 1) {
            player.getShips().remove(ship);
            System.out.println("Kapal Kita Hancur, Sedih Saya");
          } else {
            player.getShips().remove(ship);
            System.out.println("Woy Salah Serang, Rudal Terkena Kapal Sendiri!");
          }
          break;
        }
      }
    }

    if (!hit) {
      System.out.println("Kita Meleset. Ayo Serang Yang Betul-Betul!!!");
    }

    printKapal();
  }

  private void printKapal() {
    List<Coordinate> shotsPlayer1 = player1.getShotsFired();
    List<Coordinate> shotsPlayer2 = player2.getShotsFired();

    System.out
            .println(player1.getName() + " : " + player1.getShips().size() + "\t    " + player2.getName() + " : "
                    + player2.getShips().size());
    System.out.println();

    System.out.print("  |");
    for (int i = 0; i < 7; i++) {
      System.out.print(" " + (1 + i) + " |");
    }
    System.out.println();

    for (int row = 0; row < 7; row++) {
      System.out.print((row + 1) + " |");
      for (int col = 0; col < 7; col++) {
        String coordinateHistory = getCoordinateSymbol(shotsPlayer1, shotsPlayer2, row, col);

        System.out.print(" " + coordinateHistory + " |");
      }

      System.out.println();
    }
  }

  private String getCoordinateSymbol(List<Coordinate> coordinates, List<Coordinate> coordinates2, int row, int col) {
    for (Coordinate coordinate : coordinates) {
      if (coordinate.getX() == col && coordinate.getY() == row) {
        return formatCoordinate("X", "GREEN");
      }
    }

    for (Coordinate coordinate : coordinates2) {
      if (coordinate.getX() == col && coordinate.getY() == row) {
        return formatCoordinate("X", "YELLOW");
      }
    }

    return "-";
  }

  private String formatCoordinate(String coordinate, String color) {
    String ANSI_RESET = "\u001B[0m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";

    if (color.equals("GREEN")) {
      return ANSI_GREEN + coordinate + ANSI_RESET;
    } else if (color.equals("YELLOW")) {
      return ANSI_YELLOW + coordinate + ANSI_RESET;
    } else {
      return coordinate;
    }
  }

  @Override
  public boolean isGameOver() {
    return gameOver;
  }

  public static void clearScreen() {
    try {
      if (System.getProperty("os.name").contains("Windows")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        Runtime.getRuntime();
      }
    } catch (Exception e) {
      System.out.println("Error clearing the screen: " + e.getMessage());
    }
  }
}
//////////

class Coordinate {
  private int x;
  private int y;

  public Coordinate(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public String toString1() {
    return "(" + x + ", " + y + ")";
  }
}
//////////

class Player {
  private String name;
  private List<Ship> ships;
  private List<Coordinate> shotsFired;

  public Player(String name) {
    this.name = name;
    ships = new ArrayList<>();
    shotsFired = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public List<Ship> getShips() {
    return ships;
  }

  public List<Coordinate> getShotsFired() {
    return shotsFired;
  }

  public void addShip(Ship ship) {
    ships.add(ship);
  }

  public void addShotFired(Coordinate coordinate) {
    shotsFired.add(coordinate);
  }
}

//////////
class Shippp implements Ship {
  private List<Coordinate> coordinates;

  public Shippp(Coordinate coordinate) {
    coordinates = new ArrayList<>();
    coordinates.add(coordinate);
  }

  @Override
  public boolean isHit(Coordinate coordinate) {
    for (Coordinate c : coordinates) {
      if (c.getX() == coordinate.getX() && c.getY() == coordinate.getY()) {
        coordinates.remove(c);
        return true;
      }
    }
    return false;
  }

  @Override
  public void hit(Coordinate coordinate) {
    coordinates.remove(coordinate);
  }
}

//////////
interface Ship {
  boolean isHit(Coordinate coordinate);

  void hit(Coordinate coordinate);
}

//////////
interface Game {
  void start();

  void playTurn(Player player);

  boolean isGameOver();
}


