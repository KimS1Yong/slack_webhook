import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Solution02 {
    public static void main(String[] args) {
        String prompt = System.getenv("LLM_PROMPT");
//        String llmResult = useLLM("자바 알고리즘 공부를 위한 자료구조 중 랜덤으로 하나를 추천하고 설명해주는 내용을 200자 이내로 작성. 별도의 앞뒤 내용 없이 해당 내용만 출력. nutshell, for slack message, in korean.");
        String llmResult = useLLM(prompt);
        System.out.println("llmResult = " + llmResult);
//        String llmImageResult=useLLMForImage(
//                llmResult+ "를 바탕으로 해당 개념을 이해할 수 있는 상징적 과정을 표현한 비유적 이미지를 만들어줘.");
        String template=System.getenv("LLM2_IMAGE_TEMPLATE");
        // %s를 바탕으로 해당 개념을 이해할 수 있는 상징적 과정을 표현한 비유적 이미지를 만들어줘.
        String llmImageResult = useLLMForImage(template.formatted(llmResult));
        System.out.println("llmImageResult = " + llmImageResult);

        String title = System.getenv("SLACK_WEBHOOK_TITLE");
        sendSlackMessage(title, llmResult, llmImageResult);
    }

    public static String useLLMForImage(String prompt) {
        // https://api.together.xyz/
        // https://api.together.xyz/models/black-forest-labs/FLUX.1-schnell-Free

        String apiUrl = System.getenv("LLM2_API_URL"); // 환경변수로 관리
        String apiKey = System.getenv("LLM2_API_KEY"); // 환경변수로 관리
        String model = System.getenv("LLM2_MODEL"); // 환경변수로 관리
        String payload = """
                {
                  "prompt": "%s",
                  "model": "%s",
                  "width": 1440,
                  "height": 1440,
                  "steps": 4,
                  "n": 1
                }
                """.formatted(prompt, model);
        HttpClient client = HttpClient.newHttpClient(); // 새롭게 요청할 클라이언트 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl)) // URL을 통해서 어디로 요청을 보내는지 결정
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build(); // 핵심
        String result = null; // return을 하려면 일단은 할당이 되긴 해야함
        try { // try
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println("response.statusCode() = " + response.statusCode());
            System.out.println("response.body() = " + response.body());
            result = response.body()
                        .split("\"url\": \"")[1]
                        .split("\",")[0];
            /*
            {
              "id": "9154b1414e7d8b5e-ICN",
              "model": "black-forest-labs/FLUX.1-schnell-Free",
              "object": "list",
              "data": [
                {
                  "index": 0,
                  "url": "https://api.together.ai/imgproxy/jAdF66gTBXVl8zcQLzB5rBIPOMUrRyBbDH7Cm7Mi96s/format:jpeg/aHR0cHM6Ly90b2dldGhlci1haS1iZmwtaW1hZ2VzLXByb2QuczMudXMtd2VzdC0yLmFtYXpvbmF3cy5jb20vaW1hZ2VzL2FjNTM4NTJjYjg3NzI2MzUwMzc3NDdhNTNhYjViODBlYzA1NzI1YzYyNmY4ZWEwNzg2OWE4YjI5YTY3Y2E0ODM_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ29udGVudC1TaGEyNTY9VU5TSUdORUQtUEFZTE9BRCZYLUFtei1DcmVkZW50aWFsPUFTSUFZV1pXNEhWQ0pZTkdMQkxDJTJGMjAyNTAyMjElMkZ1cy13ZXN0LTIlMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwMjIxVDA2MzEyMFomWC1BbXotRXhwaXJlcz0zNjAwJlgtQW16LVNlY3VyaXR5LVRva2VuPUlRb0piM0pwWjJsdVgyVmpFS2YlMkYlMkYlMkYlMkYlMkYlMkYlMkYlMkYlMkYlMkZ3RWFDWFZ6TFhkbGMzUXRNaUpJTUVZQ0lRRCUyQmhtZmhWMHJScDVzQ0g4UWx5ckN4cWFhcSUyQkp4bEVzVm9PeDJOOGE4WUh3SWhBSlRzNklLcU5Zc0hac2xwUUxZbTh6NndxRlFucHNTSUFwejVWZ3QxTnV0aktwa0ZDTkQlMkYlMkYlMkYlMkYlMkYlMkYlMkYlMkYlMkYlMkZ3RVFBQm9NTlRrNE56STJNVFl6Tnpnd0lnekdXQUhIM012M1F3ODBNek1xN1FTRkFLVTBaZHg1NEZhOW1Tald6YkRhU29pSkdDRG5QTXZKeUMlMkJISzdPT3Y1dkdTRW9RR0VXZlcwb3luMCUyQkpXbVRTazdLQiUyRm9QQlVaR0t6cnA0MU1HbVlxSVRjNzVNWm5QRlNPJTJCakhaUVdPSk42MktNVGxCJTJCTWJnNjRCcUZVNG4yTGxVekZFN3ZNNHZsRTZCTkQ0bnVCY3o0c1pTUUZkZ2FUUjNwbWJNWHZ6RmRVQjZWOGRiTFlwQzRiQm1PcFA1ZnVTOVI4aHVkZzM4cDdvTXN6WCUyQiUyRnhiTXlQUFdTUk1tZ3FYZlgyZ1FoMGtqb2tieSUyRmRjT2VQQXNsZHV1b2JHVzYxMDBUZnBqQ25nanlCTUR2WEhnWnFkRzFlbGQzY3VuV01vQXhXb3o5RmF3dUg2JTJCYjl5T1E2VllOUzclMkY0dENnRWplUmJRMTJEYkp2R1hxeklnUm1VQUtaMEk2RGVQQ1hFQ1ZYOG93ZTBWdjJvc1MlMkZKVHZlRyUyQmlzc3NMRklFOWxzUm5XenZsZTNsNEI4d2lTOCUyRnlIVHQycHdFMUREbGUxRlhOQXcwa3kwS3NaUVhodjZnZ016N2E5cUxXNEJHM2tnWGRWenoyeWZDUEtqYk1rUmJSRjQxNUV4Sko3Zm5sNkhvWFo0QlFqUFNvbG52WFB4MkNWbmUyQTZqWiUyRnZYV1VjRlR0eHdrSzJkcmczdUtRVWFmdXN2eHdENUV2OW9aY0d3RDglMkZtTWs1MFpnS1JXd0hVTGhHbWdJMFpKQmcwdWY3RGI4cTUlMkZtWE1sYm5uVzF2VTRtWHllJTJCNkFjZU9TTGxlNmh0WnklMkZ1cVNpSzRIeVc0Y3R1ejNaUWFmSEVyZTdUM3p2SVdrMEJEeFRYODhjSlFJJTJGdUpubiUyQnQlMkJaTCUyRnRGbjYyZ2xrOVk5MVNZaFI3eXhtanNacmszMWYwdEYyb1U4c3ZJelNCNmRWaVZveTVaTXpiYWgwc2sySTduRXN4dTZzcTJMeWFBRFRMeHNSSE5pZ3JlUDlZaTklMkYlMkIlMkJXRjl2WTBRQVZvblI0aFJ4ektmck9TSWdEOVVOQ1RKR2piWXI2Q3BFWFlPR3NYTktMVWdZOEpFSnM4cXlZTDJpMkl3dDd2Z3ZRWTZtZ0VpcUNaME1BJTJCVVVmRkhxOXBINGFQVzR3ZTVLQTZsaDlQaWg2WUtMSERwdlpMc3NNZkZVaEM5TWR0R3E4ckh2MjFFRk9iZFduSGdYbTFqNjBtNHdPTTZYU3hmMWtLMHBFJTJGSUJFR0FLdDBUTnh5biUyQmRNYU5lNEVmOXdPJTJGN05KbFc3TkVCdUJUYzh1MHBxVEl5SFhUaHVkZlQ1UWxnV2NYSXVLRVIyT055SEgyZng0bkRrYzNZQzQ3U2QlMkJhcERjQ0JMM3lHbG1uZ25UWE9LUiZYLUFtei1TaWduYXR1cmU9Mzk5YjBmNTU4Y2FhMTk1YzZiZjJmMzRkZWU0NTllOWJkNzFlMmNlMTI1ZWEwZWYyZTVlY2M3ZjMzZjNmOTE0NyZYLUFtei1TaWduZWRIZWFkZXJzPWhvc3QmeC1pZD1HZXRPYmplY3Q",
                  "timings": {
                    "inference": 2.5669988580048084
                  }
                }
              ]
            }
             */

        } catch (Exception e) { // catch exception e
            throw new RuntimeException(e);
        }
        return result; // 메서드(함수)가 모두 처리되고 나서 이 값을 결과값으로 가져서 이걸 대입하거나 사용할 수 있다
    }

    public static String useLLM(String prompt) {
        // https://groq.com/
        // https://console.groq.com/playground
        // https://console.groq.com/docs/models -> production 을 권장 (사프나 포트폴리오 보자면...)
        // https://console.groq.com/docs/rate-limits -> 이중에서 왠지 일일 사용량 제한(RPD)이 빡빡한게 좋은 것일 확률이 높음
        // llama-3.3-70b-versatile -> 나중에 바뀔 가능성이 있다 없다? -> 환경변수로

        // 이름 바꾸기 -> 해당 메서드 내부? 클래스를 기준하다면 그 내부만 바꿔줌
        String apiUrl = System.getenv("LLM_API_URL"); // 환경변수로 관리
        String apiKey = System.getenv("LLM_API_KEY"); // 환경변수로 관리
        String model = System.getenv("LLM_MODEL"); // 환경변수로 관리
//        String payload = "{\"text\": \"" + prompt + "\"}";
        String payload = """
            {
              "messages": [
                {
                  "role": "user",
                  "content": "%s"
                }
              ],
              "model": "%s"
            }
            """.formatted(prompt, model);
        HttpClient client = HttpClient.newHttpClient(); // 새롭게 요청할 클라이언트 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl)) // URL을 통해서 어디로 요청을 보내는지 결정
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build(); // 핵심
        String result = null; // return을 하려면 일단은 할당이 되긴 해야함
        try { // try
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println("response.statusCode() = " + response.statusCode());
            System.out.println("response.body() = " + response.body());
            /*
            {"id":"chatcmpl-5f89fc0c-0a0c-4b55-a9ee-8a1af02ebef2",
            "object":"chat.completion",
            "created":1740115075,
            "model":"llama-3.3-70b-versatile",
            "choices":[{"index":0,"message":
            {"role":"assistant","content":"큐(Queue) 추천! FIFO 구조로 입력된 순서대로 출력, 자바 알고리즘을 공부할 때 유용한 기본 자료구조입니다."},
            "logprobs":null,"finish_reason":"stop"}],
            "usage":{...}
             */
            result = response.body()
                    .split("\"content\":\"")[1]
                    .split("\"},\"logprobs\"")[0];

        } catch (Exception e) { // catch exception e
            throw new RuntimeException(e);
        }
        return result; // 메서드(함수)가 모두 처리되고 나서 이 값을 결과값으로 가져서 이걸 대입하거나 사용할 수 있다
    }


//    public static void sendSlackMessage(String text) {
    public static void sendSlackMessage(String title, String text, String imageUrl) {
        // 다시 시작된 슬랙 침공
    //        String slackUrl = "https://hooks.slack.com/services/";
        String slackUrl = System.getenv("SLACK_WEBHOOK_URL"); // 환경변수로 관리
//        String payload = "{\"text\": \"" + text + "\"}";
        String payload = """
                    {"attachments": [{
                        "title": "%s",
                        "text": "%s",
                        "image_url": "%s"
                    }]}
                """.formatted(title, text, imageUrl);
        // 마치 브라우저나 유저인 척하는 것.
        HttpClient client = HttpClient.newHttpClient(); // 새롭게 요청할 클라이언트 생성
        // 요청을 만들어보자! (fetch)
        HttpRequest request = HttpRequest.newBuilder()
                // 어디로? URI(URL) -> Uniform Resource Identifier(Link)
                .uri(URI.create(slackUrl)) // URL을 통해서 어디로 요청을 보내는지 결정
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build(); // 핵심

        // 네트워크 과정에서 오류가 있을 수 있기에 선제적 예외처리
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            // 2는 뭔가 됨. 4,5 뭔가 잘못 됨. 1,3? 이런건 없어요 1은 볼 일이 없고요 3은..
            System.out.println("response.statusCode() = " + response.statusCode());
            System.out.println("response.body() = " + response.body());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
