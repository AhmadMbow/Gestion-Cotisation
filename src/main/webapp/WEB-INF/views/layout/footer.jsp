<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

        </div><!-- container-fluid -->
    </div><!-- page-content -->

    <!-- ===== Footer ===== -->
    <footer class="footer">
        <div class="container-fluid">
            <div class="row">
                <div class="col-sm-6">
                    <script>document.write(new Date().getFullYear())</script> &copy; Gestion Cotisations Association.
                </div>
                <div class="col-sm-6">
                    <div class="text-sm-end d-none d-sm-block">Version 1.0.0</div>
                </div>
            </div>
        </div>
    </footer>
</div><!-- main-content -->
</div><!-- layout-wrapper -->

<!-- ===== JavaScript ===== -->
<script src="${ctx}/assets/upzet/libs/jquery/jquery.min.js"></script>
<script src="${ctx}/assets/upzet/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/upzet/libs/metismenu/metisMenu.min.js"></script>
<script src="${ctx}/assets/upzet/libs/simplebar/simplebar.min.js"></script>
<script src="${ctx}/assets/upzet/libs/node-waves/waves.min.js"></script>

<!-- Chart.js -->
<script src="${ctx}/assets/upzet/libs/chart.js/chart.umd.js"></script>

<!-- DataTables -->
<script src="${ctx}/assets/upzet/libs/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${ctx}/assets/upzet/libs/datatables.net-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="${ctx}/assets/upzet/libs/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
<script src="${ctx}/assets/upzet/libs/datatables.net-responsive-bs4/js/responsive.bootstrap4.min.js"></script>

<!-- App (init sidebar / metismenu / waves) -->
<script src="${ctx}/assets/upzet/js/app.js"></script>

<script>
    $(function () {
        if ($('.datatable').length) {
            $('.datatable').DataTable({
                "responsive": true,
                "language": {
                    "url": "https://cdn.datatables.net/plug-ins/1.13.7/i18n/fr-FR.json"
                }
            });
        }
    });

    // ===== Bascule mode clair / sombre =====
    (function () {
        var html = document.documentElement;
        var btn = document.getElementById('dark-toggle');
        function refreshIcon() {
            if (!btn) return;
            var dark = html.getAttribute('data-bs-theme') === 'dark';
            btn.querySelector('i').className = (dark ? 'fas fa-sun' : 'fas fa-moon') + ' font-size-18';
        }
        refreshIcon();
        if (btn) {
            btn.addEventListener('click', function () {
                var next = html.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
                if (next === 'dark') html.setAttribute('data-bs-theme', 'dark');
                else html.removeAttribute('data-bs-theme');
                try { localStorage.setItem('theme', next); } catch (e) {}
                refreshIcon();
            });
        }
    })();
</script>

</body>
</html>
