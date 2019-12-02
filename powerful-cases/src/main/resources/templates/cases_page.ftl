<#import "/spring.ftl" as spring/>
<html lang="en">
<head> 
    <meta
     charset="utf-8"> 
    <title>powerful-cases</title> 
</head>
<body>
<div style="float:none">
    <ul>
    <#list cases as caseName>
        <li><a href="#" class="case">${caseName}</a></li>
    </#list>
    </ul>
</div>
<div>
    <div style="float:left;width:49%;">
        <textarea style="width:98%;height:60%;" class="json"></textarea>
    </div>
    <button id="execute">执行</button>
    <div style="float:right;width:48%;">
        <pre><code style="width:98%;height:60%;" class="response"></code></pre>
    </div>
</div>
</body>

<script src="/js/jquery.min.js"></script>
<script type="text/javascript">
    /*<![CDATA[*/

    $(".case").click(function () {
        $.ajax({
            type: 'GET',
            url: '/e2e/case/' +this.text,
            dataType: 'text',
            success: function (res) {
                $(".json").val(res);
            },
            error: function (res) {
                $(".json").val(res);
            },
        });
    });


    $("#execute").click(function () {
        var json=JSON.parse($(".json").val());
        $(".response").text("loading...");
        $.ajax({
            type: 'POST',
            url: '/e2e/case/execute',
            contentType: "application/json; charset=utf-8",
            dataType: 'text',
            data: JSON.stringify(json),
            success: function (res) {
                $(".response").text(res);
            },
            error: function (res) {
                $(".response").text(res.responseText);
            },
        });
    });

    /*]]>*/
</script>
</html>