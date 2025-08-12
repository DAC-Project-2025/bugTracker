import React, { useEffect, useState } from "react";
import DashbordLayout from "../../components/layout/DashbordLayout";
import { useNavigate } from "react-router-dom";
import moment from "moment/moment";
import { IoMdCard } from "react-icons/io";
import { addThousandsSeparator } from "../../utils/helper";
import InfoCard from "../../components/cards/InfoCard";
import { LuArrowRight } from "react-icons/lu";
import TaskListTable from "../../components/TaskListTable";
import CustomPieChart from "../../components/charts/CustomPieChart";
import CustomBarChart from "../../components/charts/CustomBarChart";
import ProjectsPage from "../../components/ProjectsPage";
import axios from "axios";
import {jwtDecode} from "jwt-decode"; // corrected import

const Dashboard = () => {
  const COLORS = ["#8D51FF", "#00B8DB", "#7BCE00"];
  const [userData, setUserData] = useState(null);

  const [bugs, setBugs] = useState([]);
  const [projects, setProjects] = useState([]);

  const navigate = useNavigate();

  const [pieChartData, setPieChartData] = useState([
    { status: "Completed", count: 10 },
    { status: "In-Progress", count: 5 },
    { status: "Pending", count: 8 },
  ]);

  const [barChartData, setBarChartData] = useState([
    { priority: "High", count: 3 },
    { priority: "Medium", count: 7 },
    { priority: "Low", count: 8 },
  ]);

  useEffect(() => {
    const fetchUserProfile = async () => {
      const token = localStorage.getItem("jwt");

      if (!token) {
        console.error("No token found");
        return;
      }
      const decoded = jwtDecode(token);
      console.log("decoded token authorities", decoded.authorities);

      const api = axios.create({
        baseURL: "http://localhost:8081",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      const bugApi = axios.create({
        baseURL: "http://localhost:8080",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      const projApi = axios.create({
        baseURL: "http://localhost:8083",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      try {
        const response = await api.get("/api/user/profile");
        setUserData(response.data);
      } catch (error) {
        console.error("Failed to fetch user profile", error);
      }

      try {
        const response = await bugApi.get("/api/bugs/counts");
        prepareChartData(response.data);
      } catch (error) {
        console.error("Failed to fetch bug counts", error);
      }

      try {
        const response = await bugApi.get("/api/bugs");
        setBugs(response.data);
      } catch (error) {
        console.error("Failed to fetch all bugs", error);
      }

      try {
        const response = await projApi.get("/api/projects");
        console.log("all fetched projects", response.data);
        setProjects(response.data);
      } catch (error) {
        console.error("Failed to fetch all projects", error);
      }

  
    };

    fetchUserProfile();
  }, []);

  // Prepare pie and bar chart data from API counts response
  const prepareChartData = (data) => {
    const taskDistribution = data?.taskDistribution || {};
    const taskPriorityLevels = data?.taskPriorityLevels || {};

    const taskDistributionData = [
      { status: "Pending", count: taskDistribution.pending || 5 },
      { status: "In Progress", count: taskDistribution.InProgress || 8 },
      { status: "Complete", count: taskDistribution.Complete || 10 },
    ];
    setPieChartData(taskDistributionData);

    const priorityLevelData = [
      { priority: "High", count: taskPriorityLevels.High || 5 },
      { priority: "Medium", count: taskPriorityLevels.Medium || 8 },
      { priority: "Low", count: taskPriorityLevels.Low || 3 },
    ];
    setBarChartData(priorityLevelData);
  };

  const onSeeMore = () => {
    navigate("/admin/tasks");
  };

  // Format bugs for TaskListTable
  const formattedBugs = bugs
    ? bugs.map((bug) => ({
        _id: bug.id.toString(),
        title: bug.title,
        status: bug.status ? bug.status.toLowerCase() : "pending",
        priority: bug.priority
          ? bug.priority.charAt(0).toUpperCase() + bug.priority.slice(1).toLowerCase()
          : "Low",
        createdAt: bug.createdAt,
      }))
    : [];

  // Group projects by status for ProjectsPage
  const projectsByStatus = {
    active: projects.filter(
      (p) => p.status === "OPEN" || p.status === "IN_PROGRESS"
    ),
    closed: projects.filter(
      (p) => p.status === "COMPLETED" || p.status === "CLOSED"
    ),
    all: projects,
  };

  return (
    <DashbordLayout activeMenu="dashboard">
      {console.log("admin dashboard")}
      <div className="card my-5">
        <div>
          <div className="col-span-3">
            <h2 className="text-xl md:text-2xl">Good Morning! {userData?.name}</h2>
            <p className="text-xs md:text-[13px] text-gray-400 mt-1.5">
              {moment().format("dddd Do MM YYYY")}
            </p>
          </div>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-4 gap-3 md:gap-6 mt-5">
          <InfoCard
            icon={<IoMdCard />}
            label="Total Bugs"
            value={addThousandsSeparator(
              pieChartData.reduce((acc, item) => acc + item.count, 0)
            )}
            color="bg-blue-600"
          />

          <InfoCard
            icon={<IoMdCard />}
            label="Pending Bugs"
            value={addThousandsSeparator(
              pieChartData.find((item) => item.status === "Pending")?.count || 0
            )}
            color="bg-violet-600"
          />

          <InfoCard
            icon={<IoMdCard />}
            label="In Progress"
            value={addThousandsSeparator(
              pieChartData.find((item) => item.status === "In Progress")?.count || 0
            )}
            color="bg-cyan-600"
          />

          <InfoCard
            icon={<IoMdCard />}
            label="Completed"
            value={addThousandsSeparator(
              pieChartData.find((item) => item.status === "Complete")?.count || 0
            )}
            color="bg-lime-600"
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 my-4 md:my-6">
        <div>
          <div className="card">
            <div className="flex items-center justify-between">
              <h5 className="font-medium">Bug Distribution</h5>
            </div>
            {console.log("pie data ", pieChartData)}
            <CustomPieChart data={pieChartData} color={COLORS} />
          </div>
        </div>

        <div>
          <div className="card">
            <div className="flex items-center justify-between">
              <h5 className="font-medium">Bug Priority Levels</h5>
            </div>
            {console.log("bar chart data ",barChartData)}
            <CustomBarChart data={barChartData} />
          </div>
        </div>

        <div className="md:col-span-2">
          <div className="card">
            <div className="flex items-center justify-between">
              <h5 className="text-lg">Recent Tasks</h5>
              <button className="card-btn" onClick={onSeeMore}>
                See All
                <LuArrowRight className="text-base" />
              </button>
            </div>

            <TaskListTable tableData={formattedBugs} />

            <ProjectsPage projectsByStatus={projectsByStatus} />
          </div>
        </div>
      </div>
    </DashbordLayout>
  );
};

export default Dashboard;
