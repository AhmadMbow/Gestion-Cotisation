<%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <footer class="main-footer">
        <strong>Copyright &copy; 2026
            <a href="#">Gestion Cotisations Association</a>.
        </strong>
        Tous droits réservés.
        <div class="float-right d-none d-sm-inline-block">
            <b>Version</b> 1.0.0
        </div>
    </footer>
</div>
<!-- ./wrapper -->

<script src="${pageContext.request.contextPath}/assets/plugins/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/chart.js/Chart.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/sweetalert2/sweetalert2.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/toastr/toastr.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/dist/js/adminlte.min.js"></script>

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
</script>

</body>
</html>
