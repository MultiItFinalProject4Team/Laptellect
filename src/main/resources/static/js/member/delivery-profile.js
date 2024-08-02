$(function () {
      $(document).ready(function () {

        function getAddressList() {
            $.ajax({
                url: "/api/member/deliveryList",
                type: 'GET',
                success: function(response) {
                    $('#address-box').html(response);
                },
                error: function() {
                    alert('서버 오류 실패');
                }
            });
        }

        getAddressList();

        $(".error-message").hide();
        $(".addAddressBtn").prop("disabled", true);

        $("#address-name").on("blur", function () {
          let addressName = $(this).val();
          console.log(addressName);

          if(addressName !== "") {
            $(".addAddressBtn").prop("disabled", false);
            $("#addressError").hide();
          } else {
            $(".addAddressBtn").prop("disabled", true);
            $("#addressError").show();
          }
          // 배송지 이름 빈칸 검증
        });

        $(".addAddressBtn").on("click", function () {
          function convertEmptyToNull(value) {
                  return value.trim() === "" ? null : value;
          }

          let addressName = convertEmptyToNull($('#address-name').val());
          let recipientName = convertEmptyToNull($('#recipient-name').val());
          let postalCode = convertEmptyToNull($('#postal-code').val());
          let address = convertEmptyToNull($('#address').val());
          let detailAddress = convertEmptyToNull($('#detail-address').val());
          let recipientPhone = convertEmptyToNull($('#recipient-phone').val());
          let request = convertEmptyToNull($('#request').val());

          console.log(addressName, recipientName, postalCode, address, detailAddress, recipientPhone, request);

          $.ajax({
              url: "/api/member/insert-address",
              type: "POST",
              contentType: "application/json",
              data: JSON.stringify({
                  addressName: addressName,
                  recipientName: recipientName,
                  postalCode: postalCode,
                  address: address,
                  detailAddress: detailAddress,
                  recipientPhone: recipientPhone,
                  request: request
              }),
              success: function (response) {
                  switch(response) {
                    case 1:
                        alert("배송지 등록 성공");
                        $('#inset-delivery-modal').modal('hide');
                        getAddressList();
                        break;
                    case 0:
                        alert("배송지 이름을 입력해주세요.");
                    case 101:
                        alert("배송지는 최대 10개만 등록 가능합니다.")
                    case 500:
                        alert("배송지 등록 실패.");
                        
                  }
              },
              error: function () {
                  alert("배송지 등록 실패");
              }
              // 생성 작업 수행
          });
        });
      });
    });