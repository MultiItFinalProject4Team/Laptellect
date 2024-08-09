 $(document).ready(function() {
            $('#questionSearch').on('submit', function(event) {
                event.preventDefault(); // 폼의 기본 제출 동작을 방지
                var productNo = $('#questionSearch').find('input[name="productNo"]').val();
                var key = $('#searchSelect').val();
                var keyword = $('#question_search').val();
                var type = $('#typeField').val();
                console.log(productNo+ key+ keyword+ type);

                function formatDate(dateString) {
                    var date = new Date(dateString);
                    return `${date.getFullYear().toString().slice(-2)}/${('0' + (date.getMonth() + 1)).slice(-2)}/${('0' + date.getDate()).slice(-2)} ${('0' + date.getHours()).slice(-2)}:${('0' + date.getMinutes()).slice(-2)}`;
                }

                $.ajax({
                    url:"/customer/user/getQuestionSearch",
                    type:"GET",
                    data:{productNo: productNo,
                        key: key,
                        keyword: keyword,
                        type: type
                    },
                    success: function(data){
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
                                             <h4 class="question-title">${productqList.title || '제목 없음'}</h4>
                                         </div>
                                         <div class="question-content">
                                             <p class="question-content">${productqList.content || '내용 없음'}</p>
                                         </div>
                                     </div>
                                     <hr class="question-hr">
                                 </div>
                                 `;
                                 $('#customer-list').append(newListItem);
                          });
                    }
                });
    });
 });