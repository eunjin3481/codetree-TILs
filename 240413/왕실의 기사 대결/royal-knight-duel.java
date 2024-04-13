import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;

public class Main {
    static int L, N, Q;
    static int[][] map;
    static int[][] humansMap;
    static int[][] humansMapAfterMove;
    static HashSet<Integer> movedHumans;
    static Human[] humans;

    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken()); // 체스판의 크기
        N = Integer.parseInt(st.nextToken()); // 기사의 수
        Q = Integer.parseInt(st.nextToken()); // 명령의 수

        // 체스판 정보 입력, 0: 빈칸, 1: 함정, 2: 벽
        map = new int[L][L];
        for (int i = 0; i < L; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < L; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 기사 정보 입력
        humans = new Human[N + 1];
        humansMap = new int[L][L];
        for (int n = 1; n <= N; n++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());

            humans[n] = new Human(r, c, h, w, k);

            for (int i = r; i < r + h; i++) {
                for (int j = c; j < c + w; j++) {
                    humansMap[i][j] = n;
                }
            }
        }

//        for (int i = 0; i < L; i++) {
//            System.out.println(Arrays.toString(humansMap[i]));
//        }

        // 왕의 입력
        for (int q = 1; q <= Q; q++) {
            st = new StringTokenizer(br.readLine());
            int i = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());

//            System.out.println("----" + q + "턴");

            // 체스판에 없는 기사 제외
            if (humans[i].k <= humans[i].damage) {
                continue;
            }

            // i번 기사에게 방향 d로 한 칸 이동
            pushHuman(i, d);

        }

        // Q개의 명령이 진행 된 후, 생존한 기사들이 총 받은 대미지의 합 출력
        int totalDamage = 0;
        for (int n = 1; n <= N; n++) {
            if(humans[n].k > humans[n].damage) {
                totalDamage += humans[n].damage;
            }
        }

        System.out.println(totalDamage);
    }

    public static void pushHuman(int i, int d) {
        humansMapAfterMove = new int[L][L];
        movedHumans = new HashSet<>();

        // 밀기 실패했을 경우 아무 일도 안일어남
        if (!push(i, d)) {
//            System.out.println("밀기 실패");
            return;
        }

//        System.out.println("밀기 성공");
//        for (int k = 0; k < L; k++) {
//            System.out.println(Arrays.toString(humansMapAfterMove[k]));
//        }

        // 밀린 후 상태를 업데이트
        humansMap = humansMapAfterMove;

//        for(int n : movedHumans) {
//            System.out.print("밀린 기사들: " + n);
//        }
//        System.out.println();

        // 기사들 위치와 데미지 업데이트
        updateHuman(i);

//        System.out.println("---최종 ");
//        for (int k = 0; k < L; k++) {
//            System.out.println(Arrays.toString(humansMap[k]));
//        }
//        for(Human n : humans) {
//            if(n == null) {
//                continue;
//            }
//            System.out.print(n.k - n.damage + ", ");
//        }
//        System.out.println();
    }

    public static boolean push(int i, int d) {
        Human human = humans[i];
//        System.out.println(i + "기사 " + human.r + " " + human.c);
        int sr = human.r + dr[d];
        int sc = human.c + dc[d];
        movedHumans.add(i);

        for (int row = sr; row < sr + human.h; row++) {
            for (int col = sc; col < sc + human.w; col++) {

                // 앞에 벽이 있을 경우
                if (!checkRange(row, col) || map[row][col] == 2) {
                    return false;
                }

                // 앞에 기사 있을 경우
                if (humansMap[row][col] != 0 && humansMap[row][col] != i) {
//                    System.out.println(i + "기사가 " + humansMap[row][col] + "밀려함");

                    if (!push(humansMap[row][col], d)) {
                        return false;
                    }
                }

                // 이동할 수 있는 경우
                humansMapAfterMove[row][col] = i;
            }
        }

        return true;
    }

    public static void updateHuman(int attacker) {

        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                if (humansMap[i][j] == 0) {
                    continue;
                }

                if (!movedHumans.contains(humansMap[i][j])) {
                    continue;
                }

                Human human = humans[humansMap[i][j]];

                // 위치 업데이트
                human.r = i;
                human.c = j;

                if(humansMap[i][j] == attacker) {
                    continue;
                }

                // 데미지 증가
                int damage = 0;
                for(int row = human.r; row < human.r + human.h; row ++) {
                    for(int col = human.c; col < human.c + human.w; col ++) {
                        if(map[row][col] == 1) {
                            damage++;
                        }
                    }
                }

                human.damage += damage;
                // 데미지가 체력보다 더 많을 경우
                if(human.damage >= human.k) {
                    for(int row = human.r; row < human.r + human.h; row ++) {
                        for(int col = human.c; col < human.c + human.w; col ++) {
                            humansMap[row][col] = 0;
                        }
                    }
                }

                movedHumans.remove(humansMap[i][j]);
            }
        }
    }

    public static boolean checkRange(int row, int col) {
        return row >= 0 && row < L && col >= 0 && col < L;
    }

    static class Human {
        int r, c, h, w, k, damage;

        public Human(int r, int c, int h, int w, int k) {
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
            this.damage = 0;
        }
    }
}