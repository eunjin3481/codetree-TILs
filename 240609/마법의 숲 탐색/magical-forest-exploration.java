import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
    static int R, C, K;
    static int[][] map;
    static Pos[] golemExit;
    static int[] dr = {-1, 0, 1, 0, 0};
    static int[] dc = {0, 1, 0, -1, 0};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken()); // 숲의 row 크기
        C = Integer.parseInt(st.nextToken()); // 숲의 col 크기
        K = Integer.parseInt(st.nextToken()); // 정령의 수
        map = new int[R][C];
        golemExit = new Pos[K + 1];
        int sumRow = 0;

        for (int k = 1; k <= K; k++) {
            st = new StringTokenizer(br.readLine());
            int r = -2;
            int c = Integer.parseInt(st.nextToken()) - 1; // 골렘이 출발하는 열
            int d = Integer.parseInt(st.nextToken()); // 골렘의 출구 방향 정보

            // 골렘 이동하기
            while (true) {

                // 남쪽으로 한 칸 내려갈 수 있는지 체크
                if (checkDown(r, c)) {
                    removeGolem(r, c);
                    r += 1;
                    moveGolem(r, c, k);
                }
                // 서쪽 방향으로 회전하면서 내려갈 수 있는지 체크
                else if (checkLeft(r, c)) {
                    removeGolem(r, c);
                    r += 1;
                    c -= 1;
                    d = d == 0 ? 3 : d - 1;
                    moveGolem(r, c, k);
                }
                // 동쪽 방향으로 회전하면서 내려갈 수 있는지 체크
                else if (checkRight(r, c)) {
                    removeGolem(r, c);
                    r += 1;
                    c += 1;
                    d = d == 3 ? 0 : d + 1;
                    moveGolem(r, c, k);
                }
                // 모두 다 안되면 멈추기
                else {
                    break;
                }

            }

            // 골렘의 몸 일부가 숲을 벗어난 상태라면 map 초기화
            if (r < 1) {
                map = new int[R][C];
                golemExit = new Pos[K + 1];
                continue;
            }

            // 정령 가장 남쪽의 칸으로 이동
            golemExit[k] = new Pos(r + dr[d], c + dc[d]);
            sumRow += bfs(r, c) + 1;

        }

        System.out.println(sumRow);
    }

    static int bfs(int startRow, int startCol) {
        Queue<Pos> queue = new ArrayDeque<>();
        boolean[][] isVisited = new boolean[R][C];
        queue.offer(new Pos(startRow, startCol));
        isVisited[startRow][startCol] = true;

        int maxRow = startRow;
        while (!queue.isEmpty()) {
            Pos cur = queue.poll();
            int curGolemNum = map[cur.r][cur.c];

            maxRow = Math.max(maxRow, cur.r);

            for (int d = 0; d < 4; d++) {
                int nextRow = cur.r + dr[d];
                int nextCol = cur.c + dc[d];

                // 범위를 벗어났거나, 골렘이 아닌 빈칸일 경우, 방문했을 경우
                if (!checkRange(nextRow, nextCol) || map[nextRow][nextCol] == 0 || isVisited[nextRow][nextCol]) {
                    continue;
                }

                // 다른 골렘이지만, 해당 위치가 출구가 아닌 경우
                if (map[nextRow][nextCol] != curGolemNum && (golemExit[curGolemNum].r != cur.r || golemExit[curGolemNum].c != cur.c)) {
                    continue;
                }

                queue.offer(new Pos(nextRow, nextCol));
                isVisited[nextRow][nextCol] = true;
            }
        }

        return maxRow;
    }

    static void moveGolem(int row, int col, int num) {
        for (int d = 0; d < 5; d++) {
            int nextRow = row + dr[d];
            int nextCol = col + dc[d];

            if (!checkRange(nextRow, nextCol)) {
                continue;
            }

            map[nextRow][nextCol] = num;
        }
    }

    static void removeGolem(int row, int col) {
        for (int d = 0; d < 5; d++) {
            int nextRow = row + dr[d];
            int nextCol = col + dc[d];

            if (!checkRange(nextRow, nextCol)) {
                continue;
            }

            map[nextRow][nextCol] = 0;
        }
    }

    public static boolean checkDown(int row, int col) {
        int[] rows = {row + 2, row + 1, row + 1};
        int[] cols = {col, col - 1, col + 1};

        for (int i = 0; i < 3; i++) {
            if (rows[i] >= 0 && (!checkRange(rows[i], cols[i]) || map[rows[i]][cols[i]] != 0)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkLeft(int row, int col) {
        int[] rows = {row, row - 1, row + 1, row + 1, row + 2};
        int[] cols = {col - 2, col - 1, col - 1, col - 2, col - 1};

        for (int i = 0; i < 5; i++) {
            if (rows[i] >= 0 && (!checkRange(rows[i], cols[i]) || map[rows[i]][cols[i]] != 0)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkRight(int row, int col) {
        int[] rows = {row, row - 1, row + 1, row + 1, row + 2};
        int[] cols = {col + 2, col + 1, col + 1, col + 2, col + 1};

        for (int i = 0; i < 5; i++) {
            if (rows[i] >= 0 && (!checkRange(rows[i], cols[i]) || map[rows[i]][cols[i]] != 0)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkRange(int row, int col) {
        return row >= 0 && row < R && col >= 0 && col < C;
    }


    static class Pos {
        int r;
        int c;

        public Pos(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    static class Golem {
        int r;
        int c;
        int d;


    }


}