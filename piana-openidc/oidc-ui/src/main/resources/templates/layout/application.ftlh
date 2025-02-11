<#import "/spring.ftl" as spring/>
<#macro layout>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Example</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="layout/css/panel.css" rel="stylesheet">
    <meta name="theme-color" content="#712cf9">
</head>

<body>
    <nav class="navbar navbar-default">
        <div class="container">
            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <li><a href="/one/">One</a></li>
                    <li><a href="/two/">Two</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container">
        <#nested>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
</#macro>