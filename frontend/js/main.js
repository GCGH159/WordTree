// main.js
// 负责前端与后端的Ajax交互及页面事件绑定

$(document).ready(function() {
    // 添加单词
    $('#addWord').click(function() {
        var word = $('#word').val();
        var meaning = $('#meaning').val();
        if (!word || !meaning) {
            alert('请输入单词和释义');
            return;
        }
        $.ajax({
            url: 'http://localhost:8081/api/words/addWord',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({word: word, meaning: meaning}),
            success: function(res) {
                $('#result').html('<span style="color:green">添加成功</span>');
            },
            error: function() {
                $('#result').html('<span style="color:red">添加失败</span>');
            }
        });
    });

    // 修改单词
    $('#updateWord').click(function() {
        var word = $('#word').val();
        var meaning = $('#meaning').val();
        if (!word || !meaning) {
            alert('请输入单词和释义');
            return;
        }
        $.ajax({
            url: 'http://localhost:8081/api/words/update',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({word: word, meaning: meaning}),
            success: function(res) {
                $('#result').html('<span style="color:green">修改成功</span>');
            },
            error: function() {
                $('#result').html('<span style="color:red">修改失败</span>');
            }
        });
    });

    // 查询单词
    $('#queryWord').click(function() {
        var word = $('#searchWord').val();
        if (!word) {
            alert('请输入要查询的单词');
            return;
        }
        $.ajax({
            url: 'http://localhost:8081/api/words/queryWord/' + encodeURIComponent(word),
            type: 'GET',
            success: function(res) {
                try {
                    var data = typeof res === 'string' ? JSON.parse(res) : res;
                    $('#result').empty(); // 清空之前的结果

                    if (data && !data.message) {
                        // 构建树形结构的 HTML
                        function buildTreeHtml(node, type, isRoot) {
                            var hasChildren = (node.children && node.children.length > 0);
                            var hasParents = (node.parents && node.parents.length > 0);
                            var isCollapsible = hasChildren || hasParents;

                            var liClass = 'tree-node';
                            if (isRoot) liClass += ' current-word';
                            else if (type === 'parent') liClass += ' parent-node';
                            else if (type === 'child') liClass += ' child-node';
                            if (isCollapsible) liClass += ' collapsed'; // 默认折叠

                            var html = '<li class="' + liClass + '">';
                            html += '<span class="toggler">' + (isCollapsible ? '[+]' : '&nbsp;&nbsp;&nbsp;') + '</span>'; // 如果有子节点或父节点，显示展开/折叠按钮
                            html += '<span class="content"><b>' + node.word + '</b>: ' + node.translation + '</span>';

                            if (isCollapsible) {
                                html += '<ul>';
                                if (node.parents && node.parents.length > 0) {
                                    node.parents.forEach(function(parent) {
                                        html += buildTreeHtml(parent, 'parent', false);
                                    });
                                }
                                if (node.children && node.children.length > 0) {
                                    node.children.forEach(function(child) {
                                        html += buildTreeHtml(child, 'child', false);
                                    });
                                }
                                html += '</ul>';
                            }
                            html += '</li>';
                            return html;
                        }

                        var treeRootHtml = '<ul>' + buildTreeHtml(data, 'current', true) + '</ul>';
                        $('#result').html(treeRootHtml);

                        // 添加展开/折叠事件监听
                        $('#result').off('click', '.toggler').on('click', '.toggler', function() {
                            var $toggler = $(this);
                            var $parentNode = $toggler.closest('.tree-node');
                            if ($parentNode.children('ul').length > 0) { // 确保有子列表才切换
                                $parentNode.toggleClass('collapsed');
                                if ($parentNode.hasClass('collapsed')) {
                                    $toggler.text('[+]');
                                } else {
                                    $toggler.text('[-]');
                                }
                            }
                        });

                    } else {
                        $('#result').html('<span style="color:orange">' + (data.message || '未找到该单词') + '</span>');
                    }
                } catch (e) {
                    $('#result').html('<span style="color:red">解析数据失败: ' + e.message + '</span>');
                    console.error('Error parsing data or building tree:', e);
                }
            },
            error: function() {
                $('#result').html('<span style="color:red">查询失败</span>');
            }
        });
    });
});