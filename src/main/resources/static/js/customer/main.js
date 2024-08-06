var stompClient = null;

//function setConnected(connected) {
//    $("#connect").prop("disabled", connected);
//    $("#disconnect").prop("disabled", !connected);
//    $("#send").prop("disabled", !connected);
//    if (connected) {
//        $("#conversation").show();
//    }
//    else {
//        $("#conversation").hide();
//    }
//    $("#msg").html("");
//}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    $("#conversation").show();
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        showMessage("챗봇에 오신걸 환영합니다.");
        stompClient.subscribe('/topic/public', function (message) {
            showMessage(message.body); //서버에 메시지 전달 후 리턴받는 메시지
            var formattedMessage = message.body.replace(/\n/g, "<br>");
            $('#prevmsg').val(formattedMessage);
        });
        stompClient.send("/app/sendMessage", {}, JSON.stringify('시작'));
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    if($('#msg').val() == "") {
            alert("질문을 입력하세요");
            $("#msg").focus();
            return false;
        }
    let message = $("#msg").val()
    showMessage("보낸 메시지: " + message);
    //보내기전 문자열 전처리
    let prev = $('#prevmsg').val();
    const lines = prev.split("<br>");
    for(const line of lines){
        // 문자열이 숫자로 시작하는지 확인
        if (/^\d/.test(line)) {
        let [firstPart, ...rest] = line.split(' ');
        firstPart = firstPart.replace(/\.$/, '');
        const secondPart = rest.join(' ');
        console.log('첫 부분:', firstPart);
        console.log('두 번째 부분:', secondPart);
        if(message===firstPart){
            message=secondPart;
        }
        } else {
            console.log('첫 문자가 숫자가 아닙니다.');
        }
    }

    stompClient.send("/app/sendMessage", {}, JSON.stringify(message)); //서버에 보낼 메시지
    $("#msg").val("");
    $("#msg").focus();
}

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function linkifyText(text, linkText, url) {
    // HTML 이스케이프 처리
    var escapedText = escapeHtml(text);

    // 특정 텍스트를 링크로 변환
    var linkifiedText = escapedText.replace(linkText, `<a href="${url}" target="_blank">${linkText}</a>`);
    linkifiedText=linkifiedText.split("#");

    return linkifiedText[0];
}

function showMessage(message) {
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
                console.log(message)
            }
        });
    }

    // 줄 바꿈 문자를 <br> 태그로 변환
    var formattedMessage = message.replace(/\n/g, "<br>");

    // 최종적으로 HTML 테이블에 메시지 추가
    $("#communicate").append("<tr><td>" + formattedMessage + "</td></tr>");
}

function resetMessage() {
    let message = "초기화"
    showMessage("보낸 메시지: " + message);
    //보내기전 문자열 전처리
    let prev = $('#prevmsg').val();
    const lines = prev.split("<br>");
    for(const line of lines){
        // 문자열이 숫자로 시작하는지 확인
        if (/^\d/.test(line)) {
        let [firstPart, ...rest] = line.split(' ');
        firstPart = firstPart.replace(/\.$/, '');
        const secondPart = rest.join(' ');
        console.log('첫 부분:', firstPart);
        console.log('두 번째 부분:', secondPart);
        if(message===firstPart){
            message=secondPart;
        }
        } else {
            console.log('첫 문자가 숫자가 아닙니다.');
        }
    }

    stompClient.send("/app/sendMessage", {}, JSON.stringify(message)); //서버에 보낼 메시지
    $("#msg").val("");
    $("#msg").focus();
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    connect();
//    $( "#connect" ).click(function() { connect(); });
//    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
    $( "#reset" ).click(function() { resetMessage(); });
});