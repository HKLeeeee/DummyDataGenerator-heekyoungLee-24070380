package com.ssemi.dummy;

import java.util.Scanner;

/**
 * Dummy 데이터 생성 Tool - 메인 진입점
 *
 * 메뉴:
 *   [1] 기본 Dummy 데이터 생성 (시료 5종, 주문 10건)
 *   [2] 대량 Dummy 데이터 생성 (시료 10종, 주문 50건)
 *   [3] 데이터 초기화 (파일 삭제)
 *   [0] 종료
 */
public class Main {

    public static void main(String[] args) {
        DummyDataService service = new DummyDataService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("  S-Semi Dummy Data Generator");
        System.out.println("==============================================");

        while (true) {
            printMenu();
            System.out.print("메뉴 선택: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    System.out.println("\n[기본 Dummy 데이터 생성 시작...]");
                    service.generateDefault();
                }
                case "2" -> {
                    System.out.println("\n[대량 Dummy 데이터 생성 시작...]");
                    service.generateBulk();
                }
                case "3" -> {
                    System.out.println("\n[데이터 초기화 시작...]");
                    service.reset();
                }
                case "0" -> {
                    System.out.println("\n프로그램을 종료합니다.");
                    return;
                }
                default -> System.out.println("[오류] 유효하지 않은 메뉴 번호입니다. 다시 입력하세요.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("----------------------------------------------");
        System.out.println("  [1] 기본 Dummy 데이터 생성 (시료 5종, 주문 10건)");
        System.out.println("  [2] 대량 Dummy 데이터 생성 (시료 10종, 주문 50건)");
        System.out.println("  [3] 데이터 초기화 (파일 삭제)");
        System.out.println("  [0] 종료");
        System.out.println("----------------------------------------------");
    }
}
