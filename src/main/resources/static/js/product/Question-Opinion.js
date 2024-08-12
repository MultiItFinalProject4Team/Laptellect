document.addEventListener('DOMContentLoaded', function() {
    const question = document.getElementById('getQuestionButton');
    const opinion = document.getElementById('getOpinionButton');
    const all = document.getElementById('getAllButton');
    const typeField = document.getElementById('typeField');
    var memberNo = $('#curmemberNo').val();

    function formatDate(dateString) {
        var date = new Date(dateString);
        return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
    }
    //문의 파트
    question.addEventListener('click', function() {
        typeField.value = 'question';
        loadQuestionComments(1);
    });
    //의견 파트
    opinion.addEventListener('click', function() {
        typeField.value = 'opinion';
        loadOpinionComments(1);
        });
    //전체 파트
    all.addEventListener('click', function() {
        typeField.value = 'all';
        loadComments(1);
        });

    const buttons = document.querySelectorAll('.btn-category');

        buttons.forEach(button => {
            button.addEventListener('click', function() {
                // 모든 버튼에서 active 클래스를 제거합니다.
                buttons.forEach(btn => btn.classList.remove('active'));

                // 클릭된 버튼에 active 클래스를 추가합니다.
                this.classList.add('active');
            });
        });

});

function loadQuestionComments(page) {
    var productNo = $('#curproductNo').val();
    var memberNo = $('#curmemberNo').val();
    $.ajax({
        type: "GET",
        url: `/customer/user/getQuestion`, // URL에서 잘못된 닫는 괄호 제거
        data: {
            productNo: productNo,
            page: page // 페이지 번호를 올바르게 전달
        },
        dataType: 'json', // 데이터 타입 명시
        success: function (response) {
            let commentUl = $("#customer-list");
            commentUl.empty(); // 선택 요소 비우기
            console.log("현재 페이지: "+page)
            // 페이지네이션을 업데이트하는 함수 호출
            let totalPages = response.totalPages;
            let data = response.content;
            makeQPagination(page, totalPages);

            // 댓글을 리스트에 추가
            data.forEach(function (productqList) {
            const categoryText = productqList.productqCategoryCode === 'productq_opinion' ? '의견' : '문의';
            const categoryClass = productqList.productqCategoryCode === 'productq_opinion' ? 'opinion-class' : 'question-class';
            var newListItem = `
             <div class="question">
                 <div class="question-header">
                     <div class="question-author">
                         <span class="author-name">${productqList.memberName || '정보 없음'}</span>
                         <span class="question-date">${formatDate(productqList.createdAt)}</span>
                     </div>
                 </div>
                 <div class="question-body">
                     <div class="question-bodytop">
                         <p class="question-category ${categoryClass}">${categoryText}</p>
                         <h4 class="question-title">
                             ${(productqList.secret === 'Y' && productqList.memberNo != memberNo) ? '비밀글입니다 🔒' : productqList.title}
                         </h4>
                     </div>
                     <div class="question-content">
                          <p class="question-content">
                              ${(productqList.secret === 'Y' && productqList.memberNo != memberNo) ? '본인만 확인 가능합니다' : productqList.content}
                          </p>
                     </div>
                 </div>
                 <hr class="question-hr">
             </div>
             `;
             $('#customer-list').append(newListItem);
            });
        },
        error: function () {
            alert("댓글을 로드하는 중 오류가 발생했습니다.");
        }
    });
}

function makeQPagination(page, totalPages){
    let pagination = $('#pagination');
    pagination.empty();
    startPage = 1;
    console.log("총 페이지: "+totalPages);

    if (page > 1) {
            pagination.append(`<li><a class="prev_btn" onclick="loadQuestionComments(${page - 1})">이전</a></li>`);
        } else {
            pagination.append(`<li><a class="prev_btn disabled">이전</a></li>`);
        }

    for(let i=startPage; i<=totalPages; i++) { // 페이지네이션
        if (i === page) {
            pagination.append(`<li class="active"><a>${i}</a></li>`);
        } else {
            pagination.append(`<li><a onclick="loadQuestionComments(${i})">${i}</a></li>`);
        }
    }

    if (page < totalPages) {
        pagination.append(`<li><a class="next_btn" onclick="loadQuestionComments(${page + 1})">다음</a></li>`);
    } else {
        pagination.append(`<li><a class="next_btn disabled">다음</a></li>`);
    }
}

function loadOpinionComments(page) {
    var productNo = $('#curproductNo').val();
    var memberNo = $('#curmemberNo').val();
    $.ajax({
        type: "GET",
        url: `/customer/user/getOpinion`, // URL에서 잘못된 닫는 괄호 제거
        data: {
            productNo: productNo,
            page: page // 페이지 번호를 올바르게 전달
        },
        dataType: 'json', // 데이터 타입 명시
        success: function (response) {
            let commentUl = $("#customer-list");
            commentUl.empty(); // 선택 요소 비우기
            console.log("현재 페이지: "+page)
            // 페이지네이션을 업데이트하는 함수 호출
            let totalPages = response.totalPages;
            let data = response.content;
            makeOPagination(page, totalPages);

            // 댓글을 리스트에 추가
            data.forEach(function (productqList) {
            const categoryText = productqList.productqCategoryCode === 'productq_opinion' ? '의견' : '문의';
            const categoryClass = productqList.productqCategoryCode === 'productq_opinion' ? 'opinion-class' : 'question-class';
            var newListItem = `
             <div class="question">
                 <div class="question-header">
                     <div class="question-author">
                         <span class="author-name">${productqList.memberName || '정보 없음'}</span>
                         <span class="question-date">${formatDate(productqList.createdAt)}</span>
                     </div>
                 </div>
                 <div class="question-body">
                     <div class="question-bodytop">
                         <p class="question-category ${categoryClass}">${categoryText}</p>
                         <h4 class="question-title">
                             ${(productqList.secret === 'Y' && productqList.memberNo != memberNo) ? '비밀글입니다 🔒' : productqList.title}
                         </h4>
                     </div>
                     <div class="question-content">
                          <p class="question-content">
                              ${(productqList.secret === 'Y' && productqList.memberNo != memberNo) ? '본인만 확인 가능합니다' : productqList.content}
                          </p>
                     </div>
                 </div>
                 <hr class="question-hr">
             </div>
             `;
             $('#customer-list').append(newListItem);
            });
        },
        error: function () {
            alert("댓글을 로드하는 중 오류가 발생했습니다.");
        }
    });
}

function makeOPagination(page, totalPages){
    let pagination = $('#pagination');
    pagination.empty();
    startPage = 1;
    console.log("총 페이지: "+totalPages);

    if (page > 1) {
            pagination.append(`<li><a class="prev_btn" onclick="loadOpinionComments(${page - 1})">이전</a></li>`);
        } else {
            pagination.append(`<li><a class="prev_btn disabled">이전</a></li>`);
        }

    for(let i=startPage; i<=totalPages; i++) { // 페이지네이션
        if (i === page) {
            pagination.append(`<li class="active"><a>${i}</a></li>`);
        } else {
            pagination.append(`<li><a onclick="loadOpinionComments(${i})">${i}</a></li>`);
        }
    }

    if (page < totalPages) {
        pagination.append(`<li><a class="next_btn" onclick="loadOpinionComments(${page + 1})">다음</a></li>`);
    } else {
        pagination.append(`<li><a class="next_btn disabled">다음</a></li>`);
    }
}