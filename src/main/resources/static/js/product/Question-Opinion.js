document.addEventListener('DOMContentLoaded', function() {
    const question = document.getElementById('getQuestionButton');
    const opinion = document.getElementById('getOpinionButton');
    const all = document.getElementById('getAllButton');
    const typeField = document.getElementById('typeField');
    var memberNo = $('#curmemberNo').val();

    question.addEventListener('click', function() {
    let productNo = question.getAttribute('data-product-no');
    typeField.value = 'question';

    function formatDate(dateString) {
                        var date = new Date(dateString);
                        return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
                    }

    console.log(productNo)
       $.ajax({
           url: "/customer/user/getQuestion", // 서버의 URL
           type: "GET",
           data: { productNo: productNo },
           success: function(data) {
              alert("문의"); // 성공 메시지 표시
              $('#customer-list').empty(); // 기존 내용 삭제
              console.log(data)
                 data.forEach(function(productqList) {
                 console.log(productqList)
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
           error: function(jqXHR, textStatus, errorThrown) {
               console.error('Error:', textStatus, errorThrown);
               alert('문제 발생! 다시 시도해 주세요.');
           }
       });
    });

    opinion.addEventListener('click', function() {
        let productNo = question.getAttribute('data-product-no');
        typeField.value = 'opinion';

        function formatDate(dateString) {
            var date = new Date(dateString);
            return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
        }

        console.log(productNo)
           $.ajax({
               url: "/customer/user/getOpinion", // 서버의 URL
               type: "GET",
               data: { productNo: productNo },
               success: function(data) {
                  alert("의견"); // 성공 메시지 표시
                  $('#customer-list').empty(); // 기존 내용 삭제
                  console.log(data)
                     data.forEach(function(productqList) {
                     console.log(productqList)
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
               error: function(jqXHR, textStatus, errorThrown) {
                   console.error('Error:', textStatus, errorThrown);
                   alert('문제 발생! 다시 시도해 주세요.');
               }
           });
        });

        all.addEventListener('click', function() {
                let productNo = question.getAttribute('data-product-no');
                typeField.value = 'all';

                function formatDate(dateString) {
                    var date = new Date(dateString);
                    return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
                }

                console.log(productNo)
                   $.ajax({
                       url: "/customer/user/get_AllproductqList", // 서버의 URL
                       type: "GET",
                       data: { productNo: productNo },
                       success: function(data) {
                          alert("전체"); // 성공 메시지 표시
                          $('#customer-list').empty(); // 기존 내용 삭제
                          console.log(data)
                             data.forEach(function(productqList) {
                             console.log(productqList)
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
                       error: function(jqXHR, textStatus, errorThrown) {
                           console.error('Error:', textStatus, errorThrown);
                           alert('문제 발생! 다시 시도해 주세요.');
                       }
                   });
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