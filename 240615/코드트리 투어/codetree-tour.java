import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    static final int INF = 100 * 9999;
    static int n, m;
    static StringTokenizer st;
    static ArrayList<ArrayList<Node>> graph;
    static int[] dist;
    static boolean[] isRemovedGoods = new boolean[30001];
    static PriorityQueue<Goods> goods = new PriorityQueue<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        // 명령어 실행
        int Q = Integer.parseInt(br.readLine());
        for (int q = 1; q <= Q; q++) {
            st = new StringTokenizer(br.readLine());

            int order = Integer.parseInt(st.nextToken());
            switch (order) {
                case 100:
                    buildLand();
                    break;

                case 200:
                    int id = Integer.parseInt(st.nextToken()); // 상품의 id
                    int revenue = Integer.parseInt(st.nextToken()); // 매출
                    int dest = Integer.parseInt(st.nextToken()); // 도착지
                    createGoods(id, revenue, dest);
                    break;

                case 300:
                    id = Integer.parseInt(st.nextToken());
                    removeGoods(id);
                    break;

                case 400:
                    sb.append(sellGoods()).append("\n");
                    break;

                case 500:
                    id = Integer.parseInt(st.nextToken());
                    changeStartingPoint(id);
                    break;
            }

        }
        System.out.println(sb);

    }

    public static void dijkstra(int start) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        dist = new int[n + 1];
        boolean[] isVisited = new boolean[n + 1];

        Arrays.fill(dist, INF);
        dist[start] = 0;
        pq.offer(new Node(start, 0));

        while (!pq.isEmpty()) {
            Node curNode = pq.poll();

            if (isVisited[curNode.index]) {
                continue;
            }
            isVisited[curNode.index] = true;

            for (Node nextNode : graph.get(curNode.index)) {
                if (dist[nextNode.index] > dist[curNode.index] + nextNode.cost) {
                    dist[nextNode.index] = dist[curNode.index] + nextNode.cost;
                    pq.offer(new Node(nextNode.index, dist[nextNode.index]));
                }
            }
        }
    }

    public static void buildLand() {
        n = Integer.parseInt(st.nextToken()); // 도시의 개수
        m = Integer.parseInt(st.nextToken()); // 간선의 개수

        // 그래프 생성
        graph = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < m; i++) {
            int A = Integer.parseInt(st.nextToken());
            int B = Integer.parseInt(st.nextToken());
            int cost = Integer.parseInt(st.nextToken());

            graph.get(A).add(new Node(B, cost));
            graph.get(B).add(new Node(A, cost));
        }

        // 초기 최단 거리 계산
        dijkstra(0);
    }

    public static void createGoods(int id, int revenue, int dest) {
        goods.offer(new Goods(id, revenue, dest, dist[dest]));
    }

    public static void removeGoods(int id) {
        isRemovedGoods[id] = true;
    }

    public static int sellGoods() {

        while(true) {
            // 상품이 비어있을 경우
            if(goods.isEmpty()) {
                return -1;
            }

            // 이미 삭제된 상품일 경우
            if(isRemovedGoods[goods.peek().id]) {
                goods.poll();
                continue;
            }

            // 이득이 음수일 경우
            if(goods.peek().revenue - goods.peek().dist < 0) {
                return -1;
            }

            int id = goods.poll().id;
            isRemovedGoods[id] = true;
            return id;
        }
    }

    public static void changeStartingPoint(int id) {
        dijkstra(id);

        PriorityQueue<Goods> newGoods = new PriorityQueue<>();
        while(!goods.isEmpty()) {
            Goods g = goods.poll();
            g.dist = dist[g.dest];
            newGoods.offer(g);
        }

        goods = newGoods;
    }

    static class Goods implements Comparable<Goods>{
        int id;
        int revenue;
        int dest;
        int dist;

        public Goods(int id, int revenue, int dest, int dist) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.dist = dist;
        }

        @Override
        public int compareTo(Goods o) {

            if(this.revenue - this.dist != o.revenue - o.dist) {
                return Integer.compare(o.revenue - o.dist, this.revenue - this.dist);
            }
            else {
                return Integer.compare(this.id, o.id);
            }
        }
    }

    static class Node implements Comparable<Node>{
        int index;
        int cost;

        public Node(int index, int cost) {
            this.index = index;
            this.cost = cost;
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(this.cost, o.cost);
        }
    }

}