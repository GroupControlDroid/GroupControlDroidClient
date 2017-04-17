$(document).ready(function(){
	$("input").on("keyup",function(e){
		if(e.keyCode === 13){
			login();
		}
	});

	$("#login-btn").on("click",login);

	function login(){
		var username = $("#username").val();
		var password = $("#password").val();
		$("#login-btn-text").text("登录中...");
		$.post(
			"/login.cgi",
			{
				username:username,
				password:password
			},function(data){
				if(data.code === 200){
					window.location.href = data.redirect;
				}else{
					alert(data.msg);
					$("#login-btn-text").text("登陆");
				}
			}
		);
	}

	$("#sys_notice").load("http://wechat.020fhd.com/info.php?v=1.4");
});