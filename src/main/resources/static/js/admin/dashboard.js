    google.charts.load('current', {'packages':['corechart', 'table', 'bar']});
    google.charts.setOnLoadCallback(drawCharts);

    let reviewsCurrentPage = 1;
    let questionsCurrentPage = 1;
    const itemsPerPage = 5;

    function drawCharts() {
      fetch('/admin/dashboard-data')
        .then(response => response.json())
        .then(data => {
          drawLineChart(data);
          drawDailyStats(data);
        })
        .catch(error => console.error('Error:', error));

      drawRecentReviewsTable();
      drawRecentQuestionsTable();
    }

function drawLineChart(dashboardData) {
  var data = new google.visualization.DataTable();
  data.addColumn('string', 'Date');
  data.addColumn('number', '일일 방문자');
  data.addColumn('number', '상품 판매개수');

  let maxValue = 0;
  dashboardData.forEach(item => {
    data.addRow([item.date, item.visitCount, item.saleCount]);
    maxValue = Math.max(maxValue, item.visitCount, item.saleCount);
  });

  // 최대값에 따라 적절한 눈금 간격 계산
  let tickInterval = Math.ceil(maxValue / 5);  // 대략 5개의 눈금이 생기도록 설정
  let maxTickValue = Math.ceil(maxValue / tickInterval) * tickInterval;

  var options = {
    title: '방문자 및 판매 현황',
    legend: { position: 'bottom' },
    hAxis: {
      title: '',
      slantedText: true,
      slantedTextAngle: 30,
      textStyle: {
        fontSize: 10
      }
    },
    vAxis: {
      title: '수량',
      viewWindow: {
        min: 0,
        max: maxTickValue
      },
      ticks: Array.from({length: maxTickValue/tickInterval + 1}, (_, i) => i * tickInterval)
    },
    pointSize: 5,
    chartArea: {width: '80%', height: '70%'}
  };

  var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));
  chart.draw(data, options);
}

    function drawDailyStats(dashboardData) {
      var todayData = dashboardData[dashboardData.length - 1];
      var data = google.visualization.arrayToDataTable([
        ['항목', '수량', { role: 'style' }],
        ['일일 신규회원', todayData.newMemberCount, '#0F9D58'],
        ['일일 방문자', todayData.visitCount, '#76A7FA'],
        ['일일 판매', todayData.saleCount, '#4285F4'],
        ['일일 리뷰등록', todayData.reviewCount, '#DB4437'],
        ['일일 문의등록', todayData.inquiryCount, '#F4B400']
      ]);

      var options = {
        title: '일일 지표',
        chartArea: {width: '70%', height: '70%'},
        legend: { position: 'none' },
        hAxis: {
          title: '',
          titleTextStyle: {color: '#333'}
        },
        vAxis: {
          title: '수량',
          minValue: 0,
          titleTextStyle: {color: '#333'}
        },
        bar: {groupWidth: "65%"}
      };

      var chart = new google.visualization.ColumnChart(document.getElementById('daily_stats'));
      chart.draw(data, options);
    }

    function drawRecentReviewsTable() {
      fetch('/admin/recent-reviews')
        .then(response => response.json())
        .then(reviews => {
          var data = new google.visualization.DataTable();
          data.addColumn('string', '번호');
          data.addColumn('string', '상품명');
          data.addColumn('string', '작성자');
          data.addColumn('string', '평점');
          data.addColumn('string', '작성일자');

          reviews.forEach((review) => {
            data.addRow([
              review.paymentProductReviewsNo.toString(),
              review.productName,
              review.memberName,
              review.rating.toString(),
              new Date(review.createdAt).toLocaleDateString()
            ]);
          });

          var view = new google.visualization.DataView(data);
          var totalRows = data.getNumberOfRows();
          var startIndex = (reviewsCurrentPage - 1) * itemsPerPage;
          var endIndex = Math.min(startIndex + itemsPerPage, totalRows);

          // 페이지에 표시할 행 설정
          view.setRows(Array.from({length: endIndex - startIndex}, (_, i) => startIndex + i));

          var table = new google.visualization.Table(document.getElementById('recent_reviews_table'));
          table.draw(view, {
            showRowNumber: false,
            width: '100%',
            height: '100%'
          });

          google.visualization.events.addListener(table, 'select', function() {
            var selection = table.getSelection();
            if (selection.length > 0) {
              var row = selection[0].row;
              var reviewId = view.getValue(row, 0);
              var originalIndex = reviews.findIndex(review => review.paymentProductReviewsNo.toString() === reviewId);
              if (originalIndex !== -1) {
                openReviewModal(reviews[originalIndex]);
              }
            }
          });

          updateReviewsPagination(totalRows);
        })
        .catch(error => {
          console.error('Error:', error);
          // 에러 발생 시 사용자에게 알림
          document.getElementById('recent_reviews_table').innerHTML = '리뷰를 불러오는 중 오류가 발생했습니다.';
        });
    }


    function drawRecentQuestionsTable() {
      fetch('/admin/recent-questions')
        .then(response => response.json())
        .then(questions => {
          var data = new google.visualization.DataTable();
          data.addColumn('string', '번호');
          data.addColumn('string', '제목');
          data.addColumn('string', '작성자');
          data.addColumn('string', '문의 종류');
          data.addColumn('string', '작성일자');
          data.addColumn('string', '답변 상태');

          questions.forEach((question, index) => {
            data.addRow([
              (index + 1).toString(), // 순서대로 번호 매기기
              question.title,
              question.memberName,
              question.questionType === 'personal' ? '1:1 문의' : '상품 문의',
              question.createdAt,
              question.answerStatus === 'Y' ? '답변 완료' : '미답변'
            ]);
          });

          var view = new google.visualization.DataView(data);
          var totalRows = data.getNumberOfRows();
          var startIndex = (questionsCurrentPage - 1) * itemsPerPage;
          var endIndex = Math.min(startIndex + itemsPerPage, totalRows);

          view.setRows(Array.from({length: endIndex - startIndex}, (_, i) => startIndex + i));

          var table = new google.visualization.Table(document.getElementById('recent_questions_table'));
          table.draw(view, {
            showRowNumber: false,
            width: '100%',
            height: '100%'
          });

          google.visualization.events.addListener(table, 'select', function() {
            var selection = table.getSelection();
            if (selection.length > 0) {
              var row = selection[0].row;
              var questionIndex = startIndex + row;
              if (questionIndex < questions.length) {
                openQuestionModal(questions[questionIndex]);
              }
            }
          });

          updateQuestionsPagination(totalRows);
        })
        .catch(error => {
          console.error('Error:', error);
          document.getElementById('recent_questions_table').innerHTML = '문의를 불러오는 중 오류가 발생했습니다.';
        });
    }

function openQuestionModal(question) {
  document.getElementById('modalQuestionNumber').textContent = question.questionNo;
  document.getElementById('modalQuestionAuthor').textContent = question.memberName;
  document.getElementById('modalQuestionType').textContent = question.questionType === 'personal' ? '1:1 문의' : '상품 문의';
  document.getElementById('modalQuestionCategory').textContent = question.categoryName;
  document.getElementById('modalQuestionTitle').textContent = question.title;
  document.getElementById('modalQuestionContent').textContent = question.content || '내용 없음';
  document.getElementById('modalQuestionCreateDate').textContent = question.createdAt;
  document.getElementById('modalQuestionAnswerStatus').textContent = question.answerStatus === 'Y' ? '답변 완료' : '미답변';

  document.getElementById('questionModal').style.display = 'block';
}

    function closeQuestionModal() {
      document.getElementById('questionModal').style.display = 'none';
    }

//    function deleteQuestion() {
//      const questionId = document.getElementById('modalQuestionNumber').textContent;
//      if (confirm('이 문의를 삭제하시겠습니까?')) {
//        fetch('/admin/deleteQuestion', {
//          method: 'POST',
//          headers: {
//            'Content-Type': 'application/json',
//          },
//          body: JSON.stringify({questionId: questionId}),
//        })
//        .then(response => response.json())
//        .then(data => {
//          if (data.success) {
//            alert('문의가 삭제되었습니다.');
//            closeQuestionModal();
//            drawRecentQuestionsTable();
//          } else {
//            alert('문의 삭제 중 오류가 발생했습니다.');
//          }
//        })
//        .catch(error => {
//          console.error('Error:', error);
//          alert('문의 삭제 중 오류가 발생했습니다.');
//        });
//      }
//    }

    function updateReviewsPagination(totalItems) {
      const totalPages = Math.ceil(totalItems / itemsPerPage);
      document.getElementById('reviewsPrevBtn').disabled = reviewsCurrentPage === 1;
      document.getElementById('reviewsNextBtn').disabled = reviewsCurrentPage === totalPages || totalPages === 0;
      document.getElementById('reviewsCurrentPage').textContent = totalPages > 0 ? reviewsCurrentPage : 0;
      document.getElementById('reviewsTotalPages').textContent = totalPages;
    }

    function updateQuestionsPagination(totalItems) {
      const totalPages = Math.ceil(totalItems / itemsPerPage);
      document.getElementById('questionsPrevBtn').disabled = questionsCurrentPage === 1;
      document.getElementById('questionsNextBtn').disabled = questionsCurrentPage === totalPages;
      document.getElementById('questionsCurrentPage').textContent = questionsCurrentPage;
      document.getElementById('questionsTotalPages').textContent = totalPages;
    }

    function changeReviewsPage(change) {
      reviewsCurrentPage += change;
      drawRecentReviewsTable();
    }

    function changeQuestionsPage(change) {
      questionsCurrentPage += change;
      drawRecentQuestionsTable();
    }

    function openReviewModal(review) {
      document.getElementById('modalReviewNumber').textContent = review.paymentProductReviewsNo;
      document.getElementById('modalAuthor').textContent = review.memberName;
      document.getElementById('modalProductName').textContent = review.productName;
      document.getElementById('modalRating').textContent = review.rating + '점';
      document.getElementById('modalContent').textContent = review.content;
      document.getElementById('modalCreateDate').textContent = new Date(review.createdAt).toLocaleString();
      document.getElementById('modalModifyDate').textContent = review.modifyAt ? new Date(review.modifyAt).toLocaleString() : '수정사항없음';

      document.getElementById('reviewModal').style.display = 'block';
    }

    function closeReviewModal() {
      document.getElementById('reviewModal').style.display = 'none';
    }

    function deleteReview() {
          const reviewId = document.getElementById('modalReviewNumber').textContent;
          if (confirm('이 리뷰를 삭제하시겠습니까?')) {
            fetch('/admin/deleteReviews', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
              },
              body: JSON.stringify([reviewId]),
            })
            .then(response => response.json())
            .then(data => {
              if (data.success) {
                alert('리뷰가 삭제되었습니다.');
                closeReviewModal();
                drawRecentReviewsTable();
              } else {
                alert('리뷰 삭제 중 오류가 발생했습니다.');
              }
            })
            .catch(error => {
              console.error('Error:', error);
              alert('리뷰 삭제 중 오류가 발생했습니다.');
            });
          }
        }