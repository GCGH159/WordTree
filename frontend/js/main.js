// main.js
// 负责前端与后端的Ajax交互及页面事件绑定

/**
 * 文本转语音功能
 * @param {string} text - 需要转换为语音的文本
 * @param {string} voice - 语音类型，默认为中文女声
 * @param {number} rate - 语速，默认为0
 * @param {number} pitch - 语调，默认为0
 */
function textToSpeech(text, voice = 'zh-CN-XiaoxiaoNeural', rate = 0, pitch = 0) {
    // 创建一个新的XMLHttpRequest对象
    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'https://tts.ciallo.de/api/tts', true);
    xhr.responseType = 'blob'; // 设置响应类型为blob
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.setRequestHeader('accept', 'audio/mpeg');
    
    // 当请求完成时的处理函数
    xhr.onload = function() {
        if (xhr.status === 200) {
            // 创建一个Blob对象
            const blob = new Blob([xhr.response], { type: 'audio/mpeg' });
            // 创建一个URL对象
            const url = URL.createObjectURL(blob);
            // 创建一个audio元素
            const audio = new Audio(url);
            // 播放音频
            audio.play();
            // 播放完成后释放URL对象
            audio.onended = function() {
                URL.revokeObjectURL(url);
            };
        } else {
            console.error('TTS请求失败:', xhr.status);
        }
    };
    
    // 发送请求
    xhr.send(JSON.stringify({
        text: text,
        voice: voice,
        rate: rate,
        pitch: pitch,
        preview: false
    }));
}

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
            url: 'http://localhost:8080/api/words/addWord',
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
            url: 'http://localhost:8080/api/words/update',
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
            url: 'http://localhost:8080/api/words/queryWord/' + encodeURIComponent(word),
            type: 'GET',
            success: function(res) {
                try {
                    var data = typeof res === 'string' ? JSON.parse(res) : res;
                    $('#result').empty(); // 清空之前的结果

                    if (data && !data.message) {
                        $('#result').empty(); // 清空之前的结果

                        // 构建当前单词的HTML
                        var currentWordHtml = '<div class="current-word-display">';
                        currentWordHtml += '<h2><span class="content"><b class="word-pronounce">' + data.word + '</b>: ' + data.translation + '</span></h2>';
                        currentWordHtml += '</div>';
                        $('#result').append(currentWordHtml);

                        // 辅助函数：构建单个节点的HTML（用于父节点或子节点列表）
                        function buildNodeHtml(node, type) {
                            var hasChildren = (node.children && node.children.length > 0);
                            var hasParents = (node.parents && node.parents.length > 0);
                            // 对于父/子列表中的节点，我们主要关心它自身是否有下一级子节点用于展开
                            var isCollapsible = (type === 'parent' && hasParents) || (type === 'child' && hasChildren);

                            var liClass = 'tree-node ' + type + '-node';
                            // 子节点或父节点列表中的项默认折叠其下一级
                            if (isCollapsible) liClass += ' collapsed';

                            var html = '<li class="' + liClass + '">';
                            html += '<span class="toggler">' + (isCollapsible ? '[+]' : '&nbsp;&nbsp;&nbsp;') + '</span>';
                            html += '<span class="content"><b class="word-pronounce">' + node.word + '</b>: ' + node.translation + '</span>';

                            // 如果这个节点本身还有子节点（对于子节点列表）或父节点（对于父节点列表），则递归构建
                            if (isCollapsible) {
                                html += '<ul>';
                                if (type === 'parent' && node.parents && node.parents.length > 0) {
                                    node.parents.forEach(function(p) {
                                        html += buildNodeHtml(p, 'parent'); // 递归父节点
                                    });
                                }
                                if (type === 'child' && node.children && node.children.length > 0) {
                                    node.children.forEach(function(c) {
                                        html += buildNodeHtml(c, 'child'); // 递归子节点
                                    });
                                }
                                html += '</ul>';
                            }
                            html += '</li>';
                            return html;
                        }

                        // 构建父节点区域
                        if (data.parents && data.parents.length > 0) {
                            var parentsHtml = '<div class="parent-section tree-section collapsed">';
                            parentsHtml += '<h3><span class="section-toggler">[+]</span> 父节点</h3>';
                            parentsHtml += '<ul>';
                            data.parents.forEach(function(parent) {
                                parentsHtml += buildNodeHtml(parent, 'parent');
                            });
                            parentsHtml += '</ul></div>';
                            $('#result').append(parentsHtml);
                        }

                        // 构建子节点区域
                        if (data.children && data.children.length > 0) {
                            var childrenHtml = '<div class="child-section tree-section collapsed">';
                            childrenHtml += '<h3><span class="section-toggler">[+]</span> 子节点</h3>';
                            childrenHtml += '<ul>';
                            data.children.forEach(function(child) {
                                childrenHtml += buildNodeHtml(child, 'child');
                            });
                            childrenHtml += '</ul></div>';
                            $('#result').append(childrenHtml);
                        }

                        // 添加节点展开/折叠事件监听
                        $('#result').off('click', '.toggler').on('click', '.toggler', function(e) {
                            e.stopPropagation(); // 防止事件冒泡到section-toggler
                            var $toggler = $(this);
                            var $node = $toggler.closest('.tree-node');
                            if ($node.children('ul').length > 0) {
                                $node.toggleClass('collapsed');
                                $toggler.text($node.hasClass('collapsed') ? '[+]' : '[-]');
                            }
                        });

                        // 添加区域展开/折叠事件监听
                        $('#result').off('click', '.section-toggler').on('click', '.section-toggler', function() {
                            var $toggler = $(this);
                            var $section = $toggler.closest('.tree-section');
                            $section.toggleClass('collapsed');
                            $toggler.text($section.hasClass('collapsed') ? '[+]' : '[-]');
                            // 折叠区域时，确保其内部所有节点也恢复到折叠状态的显示
                            if ($section.hasClass('collapsed')) {
                                $section.find('.tree-node:not(.collapsed)').addClass('collapsed');
                                $section.find('.toggler').text('[+]'); //  确保所有toggler都是[+]
                                $section.find('.toggler.empty').html('&nbsp;&nbsp;&nbsp;'); // 空toggler保持不变
                            }
                        });
                        
                        // 添加单词发音功能
                        $('#result').off('click', '.word-pronounce').on('click', '.word-pronounce', function() {
                            var word = $(this).text();
                            textToSpeech(word);
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