import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import "./App.css";
import ManageTask from "./pages/Admin/ManageTask";
import CreateTask from "./pages/Admin/CreateTask";
import Dashboard from "./pages/Admin/Dashboard";
import ManageUser from "./pages/Admin/ManageUser";
import Login from "./pages/Auth/Login";
import SignUp from "./pages/Auth/SignUp";
import UserDashboard from "./pages/User/UserDashboard";
import MyTasks from "./pages/User/MyTasks";
import ViewTaskDetails from "./pages/User/ViewTaskDetails";
import PrivateRoutes from "./routes/PrivateRoutes";
import KanbanBoard from "./pages/User/KanbanBoard";
import ProjectsPage from "./components/ProjectsPage";
import ProjectDetailsPage from "./components/ProjectDetailsPage";
import AddProjectForm from "./components/AddProjectForm";
import BugDetailsPage from "./components/BugDetailsPage";
import BugManagementPage from "./components/BugManagementPage";
import ProjectForm from "./components/ProjectForm";
import UserManagementPage from "./components/UserManagementPage";
import BugCreationPage from "./pages/Admin/BugCreationPage";
import PageNotFound from "./routes/PageNotFound";
import UnauthorizedPage from "./routes/UnauthorizedPage";

function App() {
  return (
    <div>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signUp" element={<SignUp />} />
          <Route path="/notfound" element={<PageNotFound />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />

          
          

          <Route
            element={<PrivateRoutes allowedRoles={["ADMIN", "MANAGER"]} />}
          >
            <Route path="/admin/dashboard" element={<Dashboard />} />
            <Route path="/admin/manage" element={<BugManagementPage />} />
            <Route path="/admin/form" element={<AddProjectForm />} />
            <Route path="/admin/manageuser" element={<UserManagementPage />} />
            <Route path="/admin/create-bug" element={<BugCreationPage />} />
            <Route path="/admin/project/:id" element={<ProjectDetailsPage />} />
            {/* other admin routes */}
          </Route>

          <Route element={<PrivateRoutes allowedRoles={["USER"]} />}>
          <Route path="/user/dashboard" element={<UserDashboard />} />
          <Route path="/user/dashboard" element={<UserDashboard />} />
          <Route path="/user/kanban" element={<KanbanBoard />} />
          <Route path="/user/all-bugs" element={<BugManagementPage />} />
          </Route>

          <Route element={<PrivateRoutes allowedRoles={["ADMIN", "MANAGER","USER"]} />}>
          <Route path="/bugdetails/:id" element={<BugDetailsPage />} />
          </Route>

          {/* <Route path="/kanban/:id" element={<ProjectDetailsPage />} />
          <Route path="/form" element={<AddProjectForm />} />
          
          <Route path="/kanban" element={<KanbanBoard />} /> */}

          {/**Public routes */}

          {/* <Route path="/admin/dashboard" element={<Dashboard />} />
          <Route path="/admin/tasks" element={<ManageTask />} />
          <Route path="/admin/dashboard" element={<Dashboard />} />
          <Route path="/admin/create-task" element={<CreateTask />} />
          <Route path="/admin/users" element={<ManageUser />} /> 
          */}

          {/**Admin routes */}
          {/* <Route
            element={<PrivateRoutes allowedRoles={["ADMIN", "MANAGER"]} />}
          >
            <Route path="/admin/dashboard" element={<Dashboard />} />
            <Route path="/project/:id" element={<ProjectDetailsPage />} />
            <Route path="/admin/tasks" element={<ManageTask />} />
            <Route path="/admin/dashboard" element={<Dashboard />} />
            <Route path="/admin/create-task" element={<CreateTask />} />
            <Route path="/admin/users" element={<ManageUser />} />
          </Route> */}

          {/**User routes */}
          <Route element={<PrivateRoutes allowedRoles={["user"]} />}>
            <Route path="/user/dashboard" element={<UserDashboard />} />
            <Route path="/user/tasks" element={<MyTasks />} />
            <Route
              path="/user/task-details/:id"
              element={<ViewTaskDetails />}
            />
            <Route path="/user/create-task" element={<CreateTask />} />
            <Route path="/user/users" element={<ManageUser />} />
          </Route>
        </Routes>
      </Router>
    </div>
  );
}

export default App;
