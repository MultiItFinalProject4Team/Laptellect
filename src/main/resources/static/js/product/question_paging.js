//각 js에 이 페이징을 적용 시켜주면 댈듯?
function loadComments(page) {
    var productNo = $('#curproductNo').val();
    $.ajax({
        type: "GET",
        url: `/customer/user/get_AllproductqList`, // URL에서 잘못된 닫는 괄호 제거
        data: {
            productNo: productNo,
            page: page // 페이지 번호를 올바르게 전달
        },
        dataType: 'json', // 데이터 타입 명시
        success: function (data) {
            let commentUl = $("#customer-list");
            commentUl.empty(); // 선택 요소 비우기
            console.log("현재 페이지: "+page)
            // 페이지네이션을 업데이트하는 함수 호출
            makePagination(data);

            // 댓글을 리스트에 추가
            data.forEach(function (productqList) {
            commentUl.append(`<h4 class="question-title">
                   ${productqList.title}
               </h4>`); // 댓글 내용을 적절히 표시
            });
        },
        error: function () {
            alert("댓글을 로드하는 중 오류가 발생했습니다.");
        }
    });
}

function makePagination(page){
    let pagination = $('#pagination');
    pagination.empty();
    startPage = 1;
    var totalPages = $('#totalPages').val();
    console.log("총 페이지: "+totalPages);
//    if(cur>0){ // 이전 버튼
//        pagination.append(`<li><a onclick="loadComments(${cur-1})">이전</a></li>`);
//    }
    for(let i=startPage; i<=totalPages; i++) { // 페이지네이션
        pagination.append(`<li><a onclick="loadComments(${i})">${i}</a></li>`);
    }
//    if(cur+1<page.totalPages){// 다음 버튼
//        pagination.append(`<li><a onclick="loadComments(${cur + 1})">다음</a></li>`);
//    }
}

$(document).ready(function () {
    loadComments(1);
    }
);