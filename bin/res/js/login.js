$(document).ready(function(){
	$("#login-btn").on("click",function(e){
		var username = $("#username").val();
		var password = $("#password").val();
		$.post(
			"/login.cgi",
			{
				username:username,
				password:password
			},function(data){
				if(data.code === 200){
					alert("登录成功");
					window.location.href = data.redirect;
				}
			}
		);
	});
});