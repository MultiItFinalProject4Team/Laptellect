let stompClient = null;
let currentRoomId = ''; // 현재 방 ID를 저장하는 변수

// 랜덤 방 ID 생성
function generateRoomId(length = 8) {
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let roomId = '';
    for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * charset.length);
        roomId += charset[randomIndex];
    }
    return roomId;
}

// 연결 함수
function connect(roomId) {
    currentRoomId = roomId; // 현재 방 ID 저장
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    $("#conversation").show();

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        showMessage("챗봇에 오신걸 환영합니다.", 'received');

        // 특정 방에 구독
        stompClient.subscribe('/topic/public/' + roomId, function (message) {
            showMessage(message.body, 'received');
            var formattedMessage = message.body.replace(/\n/g, "<br>");
            if (!formattedMessage.startsWith("잘 이해하지 못했어요.")) {
                $('#prevmsg').val(formattedMessage);
            }
        });

        // 특정 방에 시작 메시지 전송
        stompClient.send("/app/sendMessage/" + roomId, {}, JSON.stringify('시작'));
        $("#msg").focus();
    });
}

// 연결 해제 함수
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

// 메시지 전송 함수
function sendMessage() {
    // 빈 칸일 때
    if ($('#msg').val().trim() === "") {
        alert("질문을 입력하세요");
        $("#msg").focus();
        return false;
    }

    let message = $("#msg").val();
    showMessage(message, 'sent');

    // 이전에 보낸 메시지의 답변 저장 및 활용을 위한 전처리
    let prev = $('#prevmsg').val();
    const lines = prev.split("<br>");
    for (const line of lines) {
        // 문자열이 숫자로 시작하는지 확인
        if (/^\d/.test(line)) {
            let [firstPart, ...rest] = line.split(' ');
            firstPart = firstPart.replace(/\.$/, '');
            const secondPart = rest.join(' ');
            console.log('첫 부분:', firstPart);
            console.log('두 번째 부분:', secondPart);
            if (message === firstPart) {
                message = secondPart;
            }
        } else {
            console.log('첫 문자가 숫자가 아닙니다.');
        }
    }

    stompClient.send("/app/sendMessage/" + currentRoomId, {}, JSON.stringify(message)); // 서버에 보낼 메시지
    $("#msg").val("");
    $("#msg").focus();
}

// HTML 이스케이프 처리 함수
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 특정 텍스트를 링크로 변환하는 함수
function linkifyText(text, linkText, url) {
    // HTML 이스케이프 처리
    var escapedText = escapeHtml(text);

    // 특정 텍스트를 링크로 변환
    var linkifiedText = escapedText.replace(linkText, `<a href="${url}" target="_blank" class="link">${linkText}</a>`);
    linkifiedText = linkifiedText.split("#");

    return linkifiedText[0];
}

// 메시지 표시 함수
function showMessage(message, type) {
    // URL을 찾기 위한 정규 표현식
    var urlPattern = /(https?:\/\/[^\s]+)/g;

    // 메시지에서 URL을 찾기
    var urls = message.match(urlPattern);

    if (urls) {
        urls.forEach(function(url) {
            // 예시: 공지사항 이동이라는 텍스트가 있을 때 URL을 링크로 변환
            var linkText = "이동하기";
            if (message.includes(linkText)) {
                message = linkifyText(message, linkText, url);
                console.log(message);
            }
        });
    }

    // 줄 바꿈 문자를 <br> 태그로 변환
    var formattedMessage = message.replace(/\n/g, "<br>");
    var messageHtml = `<div class="message ${type}">${formattedMessage}</div>`;
    $("#communicate").append(messageHtml);
    $("#conversation").scrollTop($("#conversation")[0].scrollHeight);
}

// 메시지 초기화 및 전송 함수
function resetMessage() {
    let message = "처음으로";
    showMessage(message, 'sent');

    // 보내기 전 문자열 전처리
    let prev = $('#prevmsg').val();
    const lines = prev.split("<br>");
    for (const line of lines) {
        // 문자열이 숫자로 시작하는지 확인
        if (/^\d/.test(line)) {
            let [firstPart, ...rest] = line.split(' ');
            firstPart = firstPart.replace(/\.$/, '');
            const secondPart = rest.join(' ');
            console.log('첫 부분:', firstPart);
            console.log('두 번째 부분:', secondPart);
            if (message === firstPart) {
                message = secondPart;
            }
        } else {
            console.log('첫 문자가 숫자가 아닙니다.');
        }
    }

    stompClient.send("/app/sendMessage/" + currentRoomId, {}, JSON.stringify(message)); // 서버에 보낼 메시지
    $("#msg").val("");
    $("#msg").focus();
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    // 랜덤 방 ID 생성 및 연결
    const roomId = generateRoomId();
    connect(roomId);

    // 버튼 클릭 이벤트
    $("#send").click(function () { sendMessage(); });
    $("#reset").click(function () { resetMessage(); });
});