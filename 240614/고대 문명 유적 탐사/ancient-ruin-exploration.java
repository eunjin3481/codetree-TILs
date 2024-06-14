import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Main {
    static int K, M;
    static int idx;
    static int[][] map;
    static int[] piece;
    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, -1, 0, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        K = Integer.parseInt(st.nextToken()); // 탐사의 반복 횟수
        M = Integer.parseInt(st.nextToken()); // 벽면에 적힌 유물 조각의 개수
        map = new int[5][5];
        piece = new int[M];
        idx = 0; // piece 배열의 인덱스 번호 저장

        // 유물 번호 입력
        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 유물 조각 번호 압력
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            piece[i] = Integer.parseInt(st.nextToken());
        }

        // 탐사 반복
        ArrayList<Integer> ans = new ArrayList<>();
        for (int k = 1; k <= K; k++) {
            // 1. 탐사 진행
            int maxCnt = -1;
            int minAngle = Integer.MAX_VALUE;
            int minCol = Integer.MAX_VALUE;
            int minRow = Integer.MAX_VALUE;
            int[][] maxArr = null;

            for (int col = 1; col <= 3; col++) {
                for (int row = 1; row <= 3; row++) {
                    for (int angle = 1; angle <= 3; angle++) {
                        int[][] arr = copyArr(map);
                        int cnt = rotateArr(arr, row, col, angle);

                        if (cnt == 0) {
                            continue;
                        }
                        if (cnt > maxCnt ||
                                (cnt == maxCnt && angle < minAngle) ||
                                (cnt == maxCnt && angle == minAngle && col < minCol ||
                                        (cnt == maxCnt && angle == minAngle && col == minCol && row < minRow))) {

                            maxCnt = cnt;
                            minAngle = angle;
                            minCol = col;
                            minRow = row;
                            maxArr = arr;
                        }
                    }
                }
            }

            // 유물 획득 못하면 즉시 종료
            if (maxCnt == -1) {
                break;
            }

            // 최종 배열로 업데이트 후 조각 생성
            map = maxArr;
            int totalCnt = maxCnt;
            createPiece();

            // 2. 유물 연쇄 획득
            while (true) {
                int cnt = countNum(map);
                if (cnt == 0) {
                    break;
                }

                totalCnt += cnt;
                createPiece();
            }

            ans.add(totalCnt);
        }

        for (int cnt : ans) {
            System.out.print(cnt + " ");
        }
    }

    // 조각 생성 함수
    public static void createPiece() {
        for (int col = 0; col < 5; col++) {
            for (int row = 4; row >= 0; row--) {
                if (map[row][col] != 0) {
                    continue;
                }

                map[row][col] = piece[idx % piece.length];
                idx++;
            }
        }
    }

    // 배열 회전시키는 함수
    public static int rotateArr(int[][] arr, int row, int col, int angle) {

        // 각도만큼 배열 돌리기 반복
        for (int i = 0; i < angle; i++) {
            int[] temp = new int[3];
            temp[0] = arr[row - 1][col + 1];
            temp[1] = arr[row - 1][col];
            temp[2] = arr[row - 1][col - 1];

            // 상
            arr[row - 1][col + 1] = arr[row - 1][col - 1];
            arr[row - 1][col] = arr[row][col - 1];
            arr[row - 1][col - 1] = arr[row + 1][col - 1];

            // 좌
            arr[row - 1][col - 1] = arr[row + 1][col - 1];
            arr[row][col - 1] = arr[row + 1][col];
            arr[row + 1][col - 1] = arr[row + 1][col + 1];

            // 하
            arr[row + 1][col - 1] = arr[row + 1][col + 1];
            arr[row + 1][col] = arr[row][col + 1];
            arr[row + 1][col + 1] = arr[row - 1][col + 1];

            // 우
            arr[row + 1][col + 1] = temp[0];
            arr[row][col + 1] = temp[1];
            arr[row - 1][col + 1] = temp[2];
        }

        return countNum(arr);
    }

    // 획득 가치 카운트 함수
    public static int countNum(int[][] arr) {
        boolean[][] isVisited = new boolean[5][5];

        int cnt = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (isVisited[i][j]) {
                    continue;
                }
                cnt += bfs(arr, isVisited, i, j);
            }
        }

        return cnt;
    }

    public static int bfs(int[][] arr, boolean[][] isVisited, int row, int col) {

        int num = arr[row][col];
        ArrayList<Pos> list = new ArrayList<>();
        Queue<Pos> queue = new ArrayDeque<>();
        queue.offer(new Pos(row, col));
        isVisited[row][col] = true;
        list.add(new Pos(row, col));

        while (!queue.isEmpty()) {
            Pos cur = queue.poll();

            for (int d = 0; d < 4; d++) {
                int nextRow = cur.r + dr[d];
                int nextCol = cur.c + dc[d];

                // 범위 체크
                if (nextRow < 0 || nextRow >= 5 || nextCol < 0 || nextCol >= 5) {
                    continue;
                }

                // 방문 여부 체크
                if (isVisited[nextRow][nextCol]) {
                    continue;
                }

                // 동일한 숫자인지 체크
                if (arr[nextRow][nextCol] != num) {
                    continue;
                }

                queue.offer(new Pos(nextRow, nextCol));
                isVisited[nextRow][nextCol] = true;
                list.add(new Pos(nextRow, nextCol));
            }
        }

        if (list.size() >= 3) {
            for (Pos p : list) {
                arr[p.r][p.c] = 0;
            }

            return list.size();
        }

        return 0;

    }

    // 배열 복사 함수
    public static int[][] copyArr(int[][] originArr) {
        int[][] copiedArr = new int[originArr.length][originArr[0].length];
        for (int i = 0; i < originArr.length; i++) {
            for (int j = 0; j < originArr[0].length; j++) {
                copiedArr[i][j] = originArr[i][j];
            }
        }

        return copiedArr;
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