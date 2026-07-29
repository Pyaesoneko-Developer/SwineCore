/**
 * SwineCore — Main JavaScript
 * CSRF helpers + UI utilities
 */

/* ---- CSRF helpers for AJAX ---- */

/* Responsive safety net for legacy Thymeleaf screens. */
if (!document.querySelector('meta[name="viewport"]')) {
    var viewportMeta = document.createElement('meta');
    viewportMeta.name = 'viewport';
    viewportMeta.content = 'width=device-width, initial-scale=1';
    document.head.appendChild(viewportMeta);
}

/* System-wide icon-only feedback: no success/error message text is displayed. */
window.showIconFeedback = function(ok) {
    var overlay = document.createElement('div');
    overlay.className = 'sw-alert-overlay show';
    overlay.setAttribute('role', ok ? 'status' : 'alert');
    overlay.innerHTML = '<div class="sw-alert ' + (ok ? 'sw-alert-success' : 'sw-alert-danger') + '"><i class="bi ' + (ok ? 'bi-check-circle-fill' : 'bi-x-circle-fill') + '"></i></div>';
    document.body.appendChild(overlay);

    setTimeout(function() {
        overlay.classList.remove('show');
        overlay.classList.add('leaving');

        setTimeout(function() {
            overlay.remove();
        }, 350);
    }, 2000);
};

function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]')?.content;
}

function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]')?.content;
}

/* ---- DOM Ready ---- */
document.addEventListener('DOMContentLoaded', function() {

    /* Open separate create pages inside a consistent ERP modal. */
    var pageParams = new URLSearchParams(window.location.search);
    var insideFormModal = pageParams.get('modal') === '1';

    if (insideFormModal) {
        document.body.classList.add('erp-modal-document');
    } else if (window.bootstrap) {
        document.querySelectorAll('a[href]').forEach(function(link) {
            var url;

            try {
                url = new URL(link.href, window.location.href);
            } catch (e) {
                return;
            }

            if (url.origin !== window.location.origin || !/\/new\/?$/.test(url.pathname)) {
                return;
            }

            link.addEventListener('click', function(event) {
                event.preventDefault();

                url.searchParams.set('modal', '1');

                var host = document.getElementById('erpRemoteFormModal');

                if (!host) {
                    host = document.createElement('div');
                    host.id = 'erpRemoteFormModal';
                    host.className = 'modal fade';
                    host.tabIndex = -1;
                    host.setAttribute('data-bs-backdrop', 'static');
                    host.setAttribute('data-bs-keyboard', 'false');
                    host.innerHTML =
                        '<div class="modal-dialog modal-xl modal-dialog-centered">' +
                        '<div class="modal-content">' +
                        '<div class="modal-header"><h5 class="modal-title fw-bold">Create Record</h5></div>' +
                        '<div class="modal-body p-0"><iframe class="erp-form-frame" title="Create record"></iframe></div>' +
                        '<div class="modal-footer"><button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button></div>' +
                        '</div>' +
                        '</div>';
                    document.body.appendChild(host);
                }

                var frame = host.querySelector('iframe');
                var firstLoad = true;

                frame.onload = function() {
                    if (firstLoad) {
                        firstLoad = false;
                        return;
                    }

                    try {
                        var loaded = new URL(frame.contentWindow.location.href);

                        if (loaded.origin === window.location.origin && loaded.searchParams.get('modal') !== '1') {
                            window.location.assign(loaded.href);
                        }
                    } catch (ignored) {}
                };

                frame.src = url.href;

                bootstrap.Modal.getOrCreateInstance(host, {
                    backdrop: 'static',
                    keyboard: false
                }).show();
            });
        });
    }

    /* Convert marked inline create panels into launch-button modals. */
    if (window.bootstrap) {
        document.querySelectorAll('[data-erp-create-panel]').forEach(function(panel, index) {
            var title = panel.getAttribute('data-erp-create-panel') || 'Add Record';

            var modal = document.createElement('div');
            modal.className = 'modal fade';
            modal.id = 'erpInlineCreate' + index;
            modal.tabIndex = -1;
            modal.setAttribute('data-bs-backdrop', 'static');
            modal.setAttribute('data-bs-keyboard', 'false');

            var launch = document.createElement('button');
            launch.type = 'button';
            launch.className = 'btn btn-success mb-4';
            launch.innerHTML = '<i class="bi bi-plus-circle me-1"></i>' + title;
            launch.setAttribute('data-bs-toggle', 'modal');
            launch.setAttribute('data-bs-target', '#' + modal.id);

            panel.parentNode.insertBefore(launch, panel);
            panel.parentNode.insertBefore(modal, panel);

            modal.innerHTML =
                '<div class="modal-dialog modal-lg modal-dialog-centered">' +
                '<div class="modal-content">' +
                '<div class="modal-header"><h5 class="modal-title fw-bold"></h5></div>' +
                '<div class="modal-body"></div>' +
                '<div class="modal-footer"><button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button></div>' +
                '</div>' +
                '</div>';

            modal.querySelector('.modal-title').textContent = title;

            panel.classList.remove('mb-4');
            panel.style.maxWidth = '';
            modal.querySelector('.modal-body').appendChild(panel);
        });
    }

    /* Create dialogs close only through an explicit Cancel or a successful Submit. */
    document.querySelectorAll('.modal').forEach(function(modal) {
        var form = modal.querySelector('form[action]');

        if (!form || !/(\/create|\/purchased)(\/|$|\?)/.test(form.getAttribute('action'))) {
            return;
        }

        modal.setAttribute('data-bs-backdrop', 'static');
        modal.setAttribute('data-bs-keyboard', 'false');

        modal.querySelectorAll('.btn-close').forEach(function(button) {
            button.remove();
        });

        if (!modal.querySelector('[data-bs-dismiss="modal"]')) {
            var cancel = document.createElement('button');
            cancel.type = 'button';
            cancel.className = 'btn btn-outline-secondary';
            cancel.setAttribute('data-bs-dismiss', 'modal');
            cancel.textContent = 'Cancel';

            var footer = modal.querySelector('.modal-footer');

            if (footer) {
                footer.insertBefore(cancel, footer.firstChild);
            } else {
                form.appendChild(cancel);
            }
        }
    });

    /* Responsive ERP shell. Pure presentation; no business behavior is changed. */
    var sidebar = document.getElementById('sidebar');
    var main = document.getElementById('main-content');

    if (sidebar && main) {
        var shellBar = document.createElement('div');
        shellBar.className = 'erp-mobile-bar';
        shellBar.innerHTML =
            '<button type="button" class="erp-menu-button" aria-label="Open navigation"><i class="bi bi-list"></i></button>' +
            '<div><strong>SwineCore</strong><small>Farm ERP</small></div>';

        document.body.appendChild(shellBar);

        var backdrop = document.createElement('div');
        backdrop.className = 'erp-sidebar-backdrop';
        document.body.appendChild(backdrop);

        function toggleMenu(open) {
            sidebar.classList.toggle('mobile-open', open);
            backdrop.classList.toggle('show', open);
            document.body.classList.toggle('erp-menu-open', open);
        }

        shellBar.querySelector('button').addEventListener('click', function() {
            toggleMenu(true);
        });

        backdrop.addEventListener('click', function() {
            toggleMenu(false);
        });

        sidebar.querySelectorAll('a').forEach(function(a) {
            a.addEventListener('click', function() {
                toggleMenu(false);
            });
        });
    }

    /* Auto-dismiss compact icon feedback after two seconds. */
    document.querySelectorAll('.sw-alert-overlay').forEach(function(el) {
        requestAnimationFrame(function() {
            el.classList.add('show');
        });

        setTimeout(function() {
            el.classList.remove('show');
            el.classList.add('leaving');

            setTimeout(function() {
                el.remove();
            }, 350);
        }, 2000);
    });

    /* Add a consistent show/hide control to every password field. */
    document.querySelectorAll('input[type="password"]').forEach(function(input) {
        if (input.dataset.visibilityReady) {
            return;
        }

        input.dataset.visibilityReady = 'true';

        var wrapper = document.createElement('div');
        wrapper.className = 'password-field';

        input.parentNode.insertBefore(wrapper, input);
        wrapper.appendChild(input);

        var button = document.createElement('button');
        button.type = 'button';
        button.className = 'password-toggle';
        button.setAttribute('aria-label', 'Show password');
        button.innerHTML = '<i class="bi bi-eye"></i>';

        button.addEventListener('click', function() {
            var show = input.type === 'password';

            input.type = show ? 'text' : 'password';
            button.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
            button.innerHTML = show ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
        });

        wrapper.appendChild(button);
    });

    document.querySelectorAll('.sw-alert-danger .bi').forEach(function(icon) {
        icon.className = 'bi bi-x-circle-fill me-2';
    });

    var practicalPlaceholders = {
        name: 'Please enter name...........',
        email: 'Please enter email address...........',
        phone: 'Please enter phone number...........',
        address: 'Please enter address...........',
        description: 'Enter a operational description',
        code: 'code',
        title: 'Please enter title...........',
        reason: 'Enter the business reason............',
        comments: 'Enter review comments............',
        notes: 'Add relevant notes.............',
        quantity: 'Please enter quantity...........',
        price: 'Please enter price...........',
        amount: 'Please enter amount...........',
        total: 'Please enter total...........',
        date: 'YYYY-MM-DD',
        time: 'HH:mm',
        datetime: 'YYYY-MM-DD HH:mm',
        password: 'Enter a secure password...........',
        confirm: 'Re-enter the password...........'
    };

    document.querySelectorAll('input:not([type="hidden"]):not([type="submit"]), textarea').forEach(function(field) {
        if (field.placeholder) {
            return;
        }

        var key = (field.name || '').toLowerCase();

        var match = Object.keys(practicalPlaceholders).find(function(k) {
            return key.includes(k);
        });

        if (match) {
            field.placeholder = practicalPlaceholders[match];
        }
    });

    /*
     * Number ERP list rows consistently unless:
     * - table already has a number column
     * - table has data-no-auto-number="true"
     */
    document.querySelectorAll('table.table:not(.no-auto-number)').forEach(function(table) {
        if (table.dataset.noAutoNumber === 'true') {
            return;
        }

        var headRow = table.querySelector('thead tr');
        var bodyRows = table.querySelectorAll('tbody tr[th\\:each], tbody tr:not([th\\:if])');

        if (!headRow || !bodyRows.length) {
            return;
        }

        var firstHeading = (headRow.cells[0]?.textContent || '').trim().toLowerCase();

        if (firstHeading === '#' || firstHeading === 'no.' || firstHeading === 'no') {
            return;
        }

        var th = document.createElement('th');
        th.textContent = 'No.';
        headRow.insertBefore(th, headRow.firstChild);

        bodyRows.forEach(function(row, index) {
            if (!row.cells.length) {
                return;
            }

            var td = document.createElement('td');
            td.className = 'erp-row-number';
            td.textContent = index + 1;
            row.insertBefore(td, row.firstChild);
        });
    });

    /*
     * Currency is MMK everywhere:
     * normalize every Amount/Total/Price table column.
     */
    document.querySelectorAll('table.table').forEach(function(table) {
        var headings = Array.from(table.querySelectorAll('thead th'));

        var currencyColumns = headings.map(function(th, index) {
            return /\b(amount|total|price|subtotal|deduction)\b/i.test(th.textContent) ? index : -1;
        }).filter(function(index) {
            return index >= 0;
        });

        if (!currencyColumns.length) {
            return;
        }

        table.querySelectorAll('tbody tr').forEach(function(row) {
            currencyColumns.forEach(function(index) {
                var cell = row.cells[index];

                if (!cell) {
                    return;
                }

                var leaves = Array.from(cell.querySelectorAll('*')).filter(function(el) {
                    return !el.children.length && !/^(INPUT|SELECT|BUTTON|A)$/.test(el.tagName);
                });

                if (!leaves.length) {
                    leaves = [cell];
                }

                leaves.forEach(function(el) {
                    var text = el.textContent.trim();

                    if (!text || /MMK/i.test(text) || !/\d/.test(text)) {
                        return;
                    }

                    if (/^\$/.test(text)) {
                        text = text.replace(/^\$\s*/, '') + ' MMK';
                    } else if (/\s*\/\s*kg$/i.test(text)) {
                        text = text.replace(/\s*\/\s*kg$/i, ' MMK / kg');
                    } else {
                        text += ' MMK';
                    }

                    el.textContent = text;
                });
            });
        });
    });

    /*
     * Instant search for normal data tables.
     *
     * IMPORTANT:
     * Pages such as manager/pigs.html, manager/semen.html,
     * admin/operations.html already have custom search/filter/pagination.
     *
     * To prevent duplicate search bars, add:
     * data-no-live-search="true"
     *
     * Example:
     * <table class="table" data-no-live-search="true">
     */
    document.querySelectorAll('table.table').forEach(function(table) {
        if (table.id === 'pigTable') {
            return;
        }

        if (table.id === 'semenTable') {
            return;
        }

        if (table.dataset.noLiveSearch === 'true') {
            return;
        }

        if (table.closest('.ops-table-box')) {
            return;
        }

        if (table.dataset.liveSearchReady) {
            return;
        }

        var tbody = table.querySelector('tbody');

        if (!tbody) {
            return;
        }

        var rows = Array.from(tbody.querySelectorAll('tr')).filter(function(row) {
            return row.cells.length;
        });

        if (!rows.length) {
            return;
        }

        table.dataset.liveSearchReady = 'true';

        var wrap = table.closest('.table-responsive') || table;

        var search = document.createElement('input');
        search.type = 'search';
        search.className = 'form-control live-table-search';
        search.placeholder = 'Search this list...';
        search.setAttribute('aria-label', 'Search table');

        var slot = document.createElement('div');
        slot.className = 'erp-search-slot';
        slot.appendChild(search);

        var card = table.closest('.card');
        var scope = table.closest('#main-content, main') || document.body;
        var tablesInScope = scope.querySelectorAll('table.table').length;

        var pageHeader = Array.from(scope.querySelectorAll('.d-flex.justify-content-between')).find(function(header) {
            return tablesInScope === 1 &&
                header.querySelector('h1,h2,h3') &&
                (header.compareDocumentPosition(table) & Node.DOCUMENT_POSITION_FOLLOWING);
        });

        if (pageHeader) {
            var action = pageHeader.querySelector('a.btn,button,.btn-group,.dropdown');

            if (action) {
                slot.classList.add('erp-search-before-action');
                action.parentNode.insertBefore(slot, action);
            } else {
                pageHeader.appendChild(slot);
            }

        } else if (card && card.querySelector(':scope > .card-header')) {
            var header = card.querySelector(':scope > .card-header');
            header.classList.add('d-flex', 'align-items-center', 'gap-2');

            var headerAction = header.querySelector('a.btn,button,.btn-group,.dropdown');

            if (headerAction) {
                slot.classList.add('erp-search-before-action');
                headerAction.parentNode.insertBefore(slot, headerAction);
            } else {
                header.appendChild(slot);
            }

        } else {
            var toolbar = document.createElement('div');
            toolbar.className = 'erp-list-toolbar';
            toolbar.appendChild(slot);
            wrap.parentNode.insertBefore(toolbar, wrap);
        }

        search.addEventListener('input', function() {
            var query = search.value.trim().toLocaleLowerCase();

            rows.forEach(function(row) {
                row.hidden = query && !row.textContent.toLocaleLowerCase().includes(query);
            });
        });
    });

    /* Marked GET filters update on selection and on each debounced keystroke. */
    document.querySelectorAll('form[data-live-filter], form[action="/customer/pigs"], form[action="/customer/semen"]').forEach(function(form) {
        var timer;

        form.querySelectorAll('button[type="submit"], button:not([type])').forEach(function(button) {
            if (/apply filters/i.test(button.textContent)) {
                button.closest('[class*="col-"]')?.remove();
            }
        });

        form.querySelectorAll('select').forEach(function(select) {
            select.addEventListener('change', function() {
                form.requestSubmit();
            });
        });

        form.querySelectorAll('input[type="search"], input[name="search"]').forEach(function(input) {
            input.addEventListener('input', function() {
                clearTimeout(timer);

                timer = setTimeout(function() {
                    form.requestSubmit();
                }, 280);
            });
        });
    });

    /* Move explicit catalog searches to the upper-right, immediately before page actions. */
    document.querySelectorAll('form input[name="search"]').forEach(function(input, index) {
        var form = input.closest('form');

        if (!form || input.classList.contains('live-table-search')) {
            return;
        }

        if (!form.id) {
            form.id = 'erpSearchForm' + index;
        }

        input.setAttribute('form', form.id);

        var owner = input.closest('[class*="col-"]') || input.closest('.input-group') || input;
        var scope = form.closest('#main-content, main') || document.body;

        var header = Array.from(scope.querySelectorAll('.d-flex.justify-content-between')).find(function(item) {
            return item.querySelector('h1,h2,h3') &&
                (item.compareDocumentPosition(form) & Node.DOCUMENT_POSITION_FOLLOWING);
        });

        if (!header) {
            return;
        }

        owner.className = 'erp-explicit-search-slot';

        var action = header.querySelector('button,a.btn,.btn-group,.dropdown');

        if (action) {
            owner.classList.add('erp-search-before-action');
            action.parentNode.insertBefore(owner, action);
        } else {
            header.appendChild(owner);
        }
    });

    /* Active sidebar link highlighting */
    var path = window.location.pathname;
    var bestLink = null;

    /*
     * Semen page is part of Pig module.
     * So /manager/semen should highlight /manager/pigs sidebar link.
     */
    var activePathAlias = {
        '/manager/semen': '/manager/pigs'
    };

    var matchPath = activePathAlias[path] || path;

    document.querySelectorAll('#sidebar .nav-link').forEach(function(link) {
        var href = link.getAttribute('href');
        var clean = href ? href.split('?')[0] : '';

        if (
            clean &&
            matchPath.startsWith(clean) &&
            (!bestLink || clean.length > bestLink.getAttribute('href').split('?')[0].length)
        ) {
            bestLink = link;
        }
    });

    if (bestLink) {
        bestLink.classList.add('active');
    }

    /* Admin summary cards are navigation shortcuts to their detailed pages. */
    if (path === '/admin/dashboard') {
        var summaryRoutes = {
            'farms': '/admin/farms',
            'buildings': '/admin/farms',
            'users': '/admin/users',
            'total pigs': '/admin/analytics',
            'sold': '/admin/operations#orders',
            'for sale': '/admin/analytics'
        };

        document.querySelectorAll('.card').forEach(function(card) {
            var label = Array.from(card.querySelectorAll('.text-muted'))
                .map(function(e) {
                    return e.textContent.trim().toLowerCase();
                })
                .find(function(value) {
                    return summaryRoutes[value];
                });

            if (!label) {
                return;
            }

            card.style.cursor = 'pointer';
            card.setAttribute('role', 'link');
            card.tabIndex = 0;

            var go = function() {
                window.location.assign(summaryRoutes[label]);
            };

            card.addEventListener('click', go);

            card.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    go();
                }
            });
        });
    }
});

/* ---- Safe-delete confirmation input ---- */
document.querySelectorAll('form[data-confirm-name]').forEach(function(form) {
    form.addEventListener('submit', function(e) {
        var expected = form.dataset.confirmName;
        var input = form.querySelector('input[name="confirmName"]');

        if (!input) {
            return;
        }

        if (input.value.toLowerCase() !== expected.toLowerCase()) {
            e.preventDefault();
            window.showIconFeedback(false);
        }
    });
});