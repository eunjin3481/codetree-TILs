import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    static final int INACCESSIBLE = 2;
    static int n, m, num;
    static int[][] map;
    static Pos[] marts;
    static Human[] humans;
    static boolean[][] isVisit;
    static int[] dr = {-1, 0, 0, 1};
    static int[] dc = {0, -1, 1, 0};


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken()); // 격자의 크기
        m = Integer.parseInt(st.nextToken()); // 사람의 수
        num = 0; // 편의점 도착한 사람의 수
        map = new int[n][n];
        marts = new Pos[m + 1];
        humans = new Human[m + 1];
        

        // 격자 정보 입력
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 사람이 가고자 하는 편의점 위치 입력
        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int row = Integer.parseInt(st.nextToken())-1;
            int col = Integer.parseInt(st.nextToken())-1;
            marts[i] = new Pos(row, col);
        }

        // 출발
        int time = 0;
        while (true) {
            time++;

            // 편의점으로 가는 방향 중 가장 가까운데로 이동
            for (int i = 1; i <= m; i++) {
                if (humans[i] == null) break; // 아직 베이스캠프로 이동하지 않은 경우
                if (humans[i].isArrive) continue; // 편의점에 도착한 경우

                move(i); // 한 칸 이동
            }

            if (num == m) {
                break;
            }

            // 해당 시간의 사람 베이스 캠프로 이동
            if (time <= m) {
                //for(int i = 0; i < n; i++) {
                //    Arrays.fill(isVisit[i],false);
                //}
                isVisit = new boolean[n][n];
                goBaseCamp(time);
            }

        }

        System.out.println(time);
    }

    public static void move(int idx) {
        int minDistance = Integer.MAX_VALUE;
        int minD = -1;
        for (int d = 0; d < 4; d++) {
            int nextRow = humans[idx].r + dr[d];
            int nextCol = humans[idx].c + dc[d];

            if (!checkRange(nextRow, nextCol) || map[nextRow][nextCol] == INACCESSIBLE) {
                continue;
            }

            int distance = Math.abs(nextRow - marts[idx].r) + Math.abs(nextCol - marts[idx].c);
            if (minDistance > distance) {
                minDistance = distance;
                minD = d;
            }
        }

        humans[idx].r += dr[minD];
        humans[idx].c += dc[minD];

        if (humans[idx].r == marts[idx].r && humans[idx].c == marts[idx].c) {
            map[humans[idx].r][humans[idx].c] = INACCESSIBLE;
            humans[idx].isArrive = true;
            num++;
        }
    }


    public static void goBaseCamp(int idx) {
        Queue<Pos> queue = new ArrayDeque<>();
        queue.offer(new Pos(marts[idx].r, marts[idx].c));
        isVisit[marts[idx].r][marts[idx].c] = true;

        while (!queue.isEmpty()) {
            Pos cur = queue.poll();

            if (map[cur.r][cur.c] == 1) {
                humans[idx] = new Human(cur.r, cur.c, false); // 현재 사람의 위치 저장
                map[cur.r][cur.c] = INACCESSIBLE; // 더이상 지나갈 수 없는 곳으로 표시
                return;
            }

            for (int d = 0; d < 4; d++) {
                int nextRow = cur.r + dr[d];
                int nextCol = cur.c + dc[d];

                if (!checkRange(nextRow, nextCol) || isVisit[nextRow][nextCol] || map[nextRow][nextCol] == INACCESSIBLE) {
                    continue;
                }

                queue.offer(new Pos(nextRow, nextCol));
                isVisit[nextRow][nextCol] = true;
            }
        }
    }

    public static boolean checkRange(int row, int col) {
        return row >= 0 && row < n && col >= 0 && col < n;
    }

    static class Human {
        int r;
        int c;
        boolean isArrive;

        public Human(int r, int c, boolean isArrive) {
            this.r = r;
            this.c = c;
            this.isArrive = isArrive;
        }
    }

    static class Pos {
        int r;
        int c;

        public Pos(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}