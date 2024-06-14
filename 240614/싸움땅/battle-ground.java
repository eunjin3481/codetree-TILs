import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static int N, M, K;
    static PriorityQueue<Integer>[][] mapOfGun;
    static int[][] mapOfPlayer;
    static Player[] players;
    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        // map의 총 정보 입력
        mapOfGun = new PriorityQueue[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                mapOfGun[i][j] = new PriorityQueue<>(Collections.reverseOrder());
                int powerOfGun = Integer.parseInt(st.nextToken());
                if (powerOfGun != 0) {
                    mapOfGun[i][j].offer(powerOfGun);
                }
            }
        }

        // 플레이어 정보 입력
        players = new Player[M + 1];
        mapOfPlayer = new int[N][N];
        for (int i = 1; i <= M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());

            players[i] = new Player(x, y, d, s);
            mapOfPlayer[x][y] = i;
        }


        // 라운드 시작
        for (int k = 1; k <= K; k++) {

            // 순차적으로 플레이어 이동
            for (int m = 1; m <= M; m++) {

                movePlayer(m);
            }
        }

        // 출력
        for (Player player : players) {
            if (player == null) {
                continue;
            }
            System.out.print(player.p + " ");
        }
    }

    // 플레이어 이동시키는 함수
    public static void movePlayer(int num) {
        Player player = players[num];

        // 한 칸 이동
        changeDir(player);
        int nextRow = player.r + dr[player.d];
        int nextCol = player.c + dc[player.d];
        mapOfPlayer[player.r][player.c] = 0;
        players[num].setPos(nextRow, nextCol);

        // 플레이어가 있는 경우 배틀
        if (mapOfPlayer[nextRow][nextCol] != 0) {
            battle(mapOfPlayer[nextRow][nextCol], num);
        }
        // 플레이어가 없는 경우 총 획득
        else {
            gainGun(player);
            mapOfPlayer[nextRow][nextCol] = num;
        }
    }

    public static void movePlayer2(int num) {
        Player player = players[num];

        while (true) {
            int nextRow = player.r + dr[player.d];
            int nextCol = player.c + dc[player.d];

            // 이동하려는 칸에 다른 플레이어가 있거나 격자 번위 밖인 경우 오른쪽으로 90도씩 회전
            if (nextRow < 0 || nextCol < 0 || nextRow >= N || nextCol >= N ||
                    mapOfPlayer[nextRow][nextCol] != 0) {

                player.d = player.d == 3 ? 0 : player.d + 1;
                continue;
            }

            // 이동
            player.setPos(nextRow, nextCol);
            mapOfPlayer[nextRow][nextCol] = num;
            break;
        }

        // 이동한 위치에 총이 있는 경우 획득
        gainGun(player);
    }

    public static void battle(int APlayer, int BPlayer) {
        int powerOfAPlayer = players[APlayer].s + players[APlayer].g;
        int powerOfBPlayer = players[BPlayer].s + players[BPlayer].g;

        // 배틀
        int winPlayer = 0;
        int losePlayer = 0;
        if (powerOfAPlayer > powerOfBPlayer ||
                (powerOfAPlayer == powerOfBPlayer && players[APlayer].s > players[BPlayer].s)) {
            winPlayer = APlayer;
            losePlayer = BPlayer;
        } else if (powerOfAPlayer < powerOfBPlayer ||
                (powerOfAPlayer == powerOfBPlayer && players[APlayer].s < players[BPlayer].s)) {
            winPlayer = BPlayer;
            losePlayer = APlayer;
        }

        // 진 플레이어는 총 내려놓고 이동
        if (players[losePlayer].g != 0) {
            mapOfGun[players[losePlayer].r][players[losePlayer].c].offer(players[losePlayer].g);
            players[losePlayer].g = 0;
        }
        movePlayer2(losePlayer);

        // 이긴 플레이어는 포인트 획득 후 총 다시 획득
        players[winPlayer].p += Math.abs(powerOfAPlayer - powerOfBPlayer);
        gainGun(players[winPlayer]);
        mapOfPlayer[players[winPlayer].r][players[winPlayer].c] = winPlayer;

    }

    public static void gainGun(Player player) {
        if (!mapOfGun[player.r][player.c].isEmpty()) {
            mapOfGun[player.r][player.c].offer(player.g);
            player.g = mapOfGun[player.r][player.c].poll();
        }
    }

    public static void changeDir(Player player) {

        if (player.d == 0 && player.r == 0) {
            player.d = 2;
        } else if (player.d == 1 && player.c == N - 1) {
            player.d = 3;
        } else if (player.d == 2 && player.r == N - 1) {
            player.d = 0;
        } else if (player.d == 3 && player.c == 0) {
            player.d = 1;
        }
    }

    static class Player {
        int r;
        int c;
        int d;
        int s;
        int g;
        int p;

        public Player(int r, int c, int d, int s) {
            this.r = r;
            this.c = c;
            this.d = d;
            this.s = s;
            this.g = 0;
            this.p = 0;
        }

        public void setPos(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}