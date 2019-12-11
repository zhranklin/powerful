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
      <div>
        <input style="width:98%;" class="params" type="text">
      </div>
      <div>
        <textarea style="width:98%;height:60%;" class="json"></textarea>
      </div>
    </div>
    <button id="execute">执行</button>
    <div style="float:right;width:48%;">
        <pre><code style="width:98%;height:60%;" class="response"></code></pre>
    </div>
</div>
</body>

<script src="/js/jquery.min.js"></script>
<script src="/js/esprima.js"></script>
<script src="/js/js-yaml.min.js"></script>
<script type="text/javascript">
    /*<![CDATA[*/

    $(".case").click(function () {
        $.ajax({
            type: 'GET',
            url: '/c/' +this.text,
            dataType: 'text',
            success: function (res) {
                $(".json").val(jsyaml.dump(JSON.parse(res)));
            },
            error: function (res) {
                $(".json").val(res);
            },
        });
    });


    $("#execute").click(function () {
        var json = jsyaml.load($(".json").val());
        $(".response").text("loading...");
        var url = '/e';
        var params = $(".params").val();
        if (params) {
            url = url + "?" + params
        }
        $.ajax({
            type: 'POST',
            url: url,
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