import { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { motion } from "framer-motion";
import axios from "axios";
import { FiPlus, FiCheck, FiTrash2 } from "react-icons/fi";
import DashbordLayout from "./layout/DashbordLayout";

const ProjectDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const initialProject = location.state?.project || null;
  const [project, setProject] = useState(initialProject);
  const [loading, setLoading] = useState(!initialProject);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editedProject, setEditedProject] = useState({});

  // Milestones state
  const [milestones, setMilestones] = useState([]);
  const [newMilestoneName, setNewMilestoneName] = useState("");

  // Bugs state
  const [bugs, setBugs] = useState([]);
  const [loadingBugs, setLoadingBugs] = useState(true);
  const [errorBugs, setErrorBugs] = useState(null);

  const handleDelete =async(id)=>{
    const token = localStorage.getItem("jwt")
    const api = axios.create({
      baseURL: "http://localhost:8083",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
    })

    try {
      const response = api.delete(`/api/bugs/${id}`);
    } catch (error) {
      console.log("failed to delete")
    }
  }

  const deleteBug = (id) => {
    setBugs((prev) => prev.filter((bug) => bug.id !== id));
    handleDelete(id)
  };

  const handleUpdateProject = async () => {
    const token = localStorage.getItem("jwt");
    const api = axios.create({
      baseURL: "http://localhost:8083",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    try {
      const response = await api.put(`/api/projects/${id}`, editedProject);
      setProject(response.data);
      setIsEditing(false);
    } catch (error) {
      console.error("Failed to update project", error);
      alert("Failed to update project. Please try again.");
    }
  };

  const handleCancelEdit = () => {
    setEditedProject(project);
    setIsEditing(false);
  };

  // Load project and milestones
  useEffect(() => {
    if (project) {
      // Load milestones from localStorage
      const storedMilestones = localStorage.getItem(`milestones_${id}`);
      if (storedMilestones) {
        setMilestones(JSON.parse(storedMilestones));
      } else if (project.milestones) {
        setMilestones(
          project.milestones.map((m) => ({
            name: m.name,
            completed: m.completed || false,
            date: m.date || null,
          }))
        );
      }
      setEditedProject(project);
      setLoading(false);
      return;
    }

    // Fetch project from API
    const fetchProjectDetails = async () => {
      const token = localStorage.getItem("jwt");
      const api = axios.create({
        baseURL: "http://localhost:8083",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      try {
        const response = await api.get(`/api/projects/${id}`);
        setProject(response.data);
        setEditedProject(response.data);

        // Load milestones similarly
        const storedMilestones = localStorage.getItem(`milestones_${id}`);
        if (storedMilestones) {
          setMilestones(JSON.parse(storedMilestones));
        } else if (response.data.milestones) {
          setMilestones(
            response.data.milestones.map((m) => ({
              name: m.name,
              completed: m.completed || false,
              date: m.date || null,
            }))
          );
        }
      } catch (err) {
        console.error("Failed to fetch the data from server", err);
        setError("Failed to load project data.");
      } finally {
        setLoading(false);
      }
    };

    fetchProjectDetails();
  }, [id, project]);

  // Load bugs related to the project
  useEffect(() => {
    const fetchBugs = async () => {
      const token = localStorage.getItem("jwt");
      const bugApi = axios.create({
        baseURL: "http://localhost:8080",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      try {
        const response = await bugApi.get(`/api/bugs/project/${id}`);
        setBugs(response.data);
      } catch (err) {
        console.error("Failed to fetch bugs", err);
        setErrorBugs("Failed to load bugs.");
      } finally {
        setLoadingBugs(false);
      }
    };

    fetchBugs();
  }, [id]);

  // Sync milestones changes to localStorage
  useEffect(() => {
    localStorage.setItem(`milestones_${id}`, JSON.stringify(milestones));
  }, [milestones, id]);

  // Toggle milestone completion
  const toggleMilestone = (index) => {
    setMilestones((prev) =>
      prev.map((m, i) =>
        i === index
          ? {
              ...m,
              completed: !m.completed,
              date: !m.completed
                ? new Date().toISOString().split("T")[0]
                : null,
            }
          : m
      )
    );
  };

  // Delete milestone
  const deleteMilestone = (index) => {
    setMilestones((prev) => prev.filter((_, i) => i !== index));
  };

  // Add milestone
  const addMilestone = () => {
    if (newMilestoneName.trim() === "") return;

    if (
      milestones.find(
        (m) => m.name.toLowerCase() === newMilestoneName.trim().toLowerCase()
      )
    ) {
      alert("Milestone with this name already exists.");
      return;
    }

    setMilestones((prev) => [
      ...prev,
      { name: newMilestoneName.trim(), completed: false, date: null },
    ]);
    setNewMilestoneName("");
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-red-600 mb-4">{error}</h2>
          <button
            onClick={() => navigate(-1)}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            Back to Projects
          </button>
        </div>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">
            Project Not Found
          </h2>
          <button
            onClick={() => navigate(-1)}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
          >
            Back to Projects
          </button>
        </div>
      </div>
    );
  }
  function calculateDaysRemaining(dueDate) {
    const today = new Date();
    const due = new Date(dueDate);
    const diffTime = due - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays > 0) {
      return `${diffDays} days left`;
    } else if (diffDays === 0) {
      return "Due Today";
    } else {
      return `${Math.abs(diffDays)} days overdue`;
    }
  }

  const getStatusBadge = () => {
    switch (project.status) {
      case "OPEN":
        return (
          <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
            In Progress
          </span>
        );
      case "CLOSED":
        return (
          <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
            Completed
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
            Planning
          </span>
        );
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "open":
        return "bg-red-100 text-red-800";
      case "in-progress":
        return "bg-blue-100 text-blue-800";
      case "resolved":
        return "bg-green-100 text-green-800";
      case "rejected":
        return "bg-gray-100 text-gray-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };


  const getPriorityColor = (priority)=>{
    switch (priority) {
      case "High":
        return "bg-red-100 text-red-800";
      case "Medium":
        return "bg-blue-100 text-yellow-800";
      case "low":
        return "bg-green-100 text-green-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  }

  return (
    <DashbordLayout>
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
          >
            <div className="flex justify-between items-start">
              <div>
                <button
                  onClick={() => navigate(-1)}
                  className="flex items-center text-gray-600 hover:text-gray-900 mb-4 transition"
                >
                  <svg
                    className="w-5 h-5 mr-1"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M10 19l-7-7m0 0l7-7m-7 7h18"
                    ></path>
                  </svg>
                  Back to Projects
                </button>

                {isEditing ? (
                  <div className="space-y-4">
                    <input
                      type="text"
                      value={editedProject.title || ''}
                      onChange={(e) => setEditedProject({...editedProject, title: e.target.value})}
                      className="text-3xl font-bold text-gray-900 border border-gray-300 rounded px-3 py-2 w-full"
                    />
                  </div>
                ) : (
                  <h1 className="text-3xl font-bold text-gray-900">
                    {project.title}
                  </h1>
                )}
              </div>

              <div className="flex items-center space-x-4">
                {isEditing ? (
                  <div className="flex space-x-2">
                    <button
                      onClick={handleUpdateProject}
                      className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition"
                    >
                      Save
                    </button>
                    <button
                      onClick={handleCancelEdit}
                      className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition"
                    >
                      Cancel
                    </button>
                  </div>
                ) : (
                  <div className="flex items-center space-x-4">
                    <button
                      onClick={() => setIsEditing(true)}
                      className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                    >
                      Edit Project
                    </button>
                    {getStatusBadge()}
                    <div className="text-sm text-gray-500">
                      {project.status === "CLOSED" ? (
                        <span>Completed on {project.completedDate}</span>
                      ) : (
                        <span>Due on {project.dueDate}</span>
                      )}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </motion.div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Project Overview */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.3, delay: 0.1 }}
            >
              {/* Project Description Card */}
              <div className="bg-white rounded-xl shadow-md p-6 mb-6 border-l-4 border-blue-500">
                <h2 className="text-xl font-semibold text-gray-800 mb-3">
                  Project Overview
                </h2>
                {isEditing ? (
                  <textarea
                    value={editedProject.description || ''}
                    onChange={(e) => setEditedProject({...editedProject, description: e.target.value})}
                    className="w-full h-32 border border-gray-300 rounded px-3 py-2 text-gray-600"
                  />
                ) : (
                  <p className="text-gray-600">{project.description}</p>
                )}
              </div>

              {/* Project Metadata Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {/* Category Card */}
                <div className="bg-white/90 backdrop-blur-sm rounded-lg p-4 shadow-sm border border-gray-100">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-blue-100 rounded-lg text-blue-600">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M4 5a1 1 0 011-1h14a1 1 0 011 1v2a1 1 0 01-1 1H5a1 1 0 01-1-1V5zM4 13a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H5a1 1 0 01-1-1v-6zM16 13a1 1 0 011-1h2a1 1 0 011 1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-6z"
                        />
                      </svg>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Category</p>
                      <p className="font-medium text-gray-800">
                        {project.category}
                      </p>
                    </div>
                  </div>
                </div>

                {/* Status Card */}
                <div className="bg-white/90 backdrop-blur-sm rounded-lg p-4 shadow-sm border border-gray-100">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-purple-100 rounded-lg text-purple-600">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                        />
                      </svg>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Status</p>
                      {isEditing ? (
                        <select
                          value={editedProject.status || ''}
                          onChange={(e) => setEditedProject({...editedProject, status: e.target.value})}
                          className="border border-gray-300 rounded px-2 py-1 text-sm"
                        >
                          <option value="OPEN">OPEN</option>
                          <option value="CLOSED">CLOSED</option>
                          <option value="IN_PROGRESS">IN_PROGRESS</option>
                        </select>
                      ) : (
                        <div className="flex items-center">
                          <p className="font-medium text-gray-800 mr-2">
                            {project.status}
                          </p>
                          <span
                            className={`px-2 py-0.5 text-xs rounded-full ${
                              project.status === "OPEN"
                                ? "bg-green-100 text-green-800"
                                : "bg-yellow-100 text-yellow-800"
                            }`}
                          >
                            {project.status === "OPEN" ? "Active" : "Closed"}
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                {/* Priority Card */}
                <div className="bg-white/90 backdrop-blur-sm rounded-lg p-4 shadow-sm border border-gray-100">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-red-100 rounded-lg text-red-600">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01"
                        />
                      </svg>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Priority</p>
                      {isEditing ? (
                        <select
                          value={editedProject.priority || ''}
                          onChange={(e) => setEditedProject({...editedProject, priority: e.target.value})}
                          className="border border-gray-300 rounded px-2 py-1 text-sm"
                        >
                          <option value="HIGH">HIGH</option>
                          <option value="MEDIUM">MEDIUM</option>
                          <option value="LOW">LOW</option>
                        </select>
                      ) : (
                        <div className="flex items-center">
                          <p className="font-medium text-gray-800">
                            {project.priority}
                          </p>
                          {project.priority === "HIGH" && (
                            <span className="ml-2 animate-pulse text-xs px-2 py-0.5 bg-red-100 text-red-800 rounded-full">
                              Urgent
                            </span>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                {/* Start Date Card */}
                <div className="bg-white/90 backdrop-blur-sm rounded-lg p-4 shadow-sm border border-gray-100">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-amber-100 rounded-lg text-amber-600">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                        />
                      </svg>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Start Date</p>
                      <p className="font-medium text-gray-800">
                        {project.startDate}
                      </p>
                    </div>
                  </div>
                </div>

                {/* Due Date Card */}
                <div className="bg-white/90 backdrop-blur-sm rounded-lg p-4 shadow-sm border border-gray-100">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-indigo-100 rounded-lg text-indigo-600">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-5 w-5"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth="2"
                          d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                        />
                      </svg>
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Due Date</p>
                      {isEditing ? (
                        <input
                          type="date"
                          value={editedProject.dueDate || ''}
                          onChange={(e) => setEditedProject({...editedProject, dueDate: e.target.value})}
                          className="border border-gray-300 rounded px-2 py-1 text-sm"
                        />
                      ) : (
                        <div className="flex items-center">
                          <p className="font-medium text-gray-800">
                            {project.dueDate}
                          </p>
                          <div className="ml-2 text-xs px-2 py-0.5 bg-indigo-100 text-indigo-800 rounded-full">
                            {calculateDaysRemaining(project.dueDate)}
                          </div>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </motion.div>

            {/* Milestones */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.3, delay: 0.2 }}
              className="bg-white shadow rounded-lg p-6 mb-8 mt-10"
            >
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-semibold text-gray-800">
                  Milestones
                </h2>
                <span className="text-sm bg-blue-100 text-blue-800 px-2 py-1 rounded-full">
                  {milestones.filter((m) => m.completed).length}/
                  {milestones.length} completed
                </span>
              </div>

              <div className="space-y-3 mb-6">
                {milestones.map((milestone, index) => (
                  <motion.div
                    key={index}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.2 }}
                    className="flex items-start group"
                  >
                    <button
                      onClick={() => toggleMilestone(index)}
                      className={`flex-shrink-0 h-5 w-5 rounded-full mt-1 mr-3 flex items-center justify-center transition-all ${
                        milestone.completed
                          ? "bg-green-500 hover:bg-green-600"
                          : "bg-gray-200 hover:bg-gray-300"
                      }`}
                    >
                      {milestone.completed && (
                        <FiCheck className="h-3 w-3 text-white" />
                      )}
                    </button>

                    <div className="flex-grow">
                      <div className="flex justify-between items-center">
                        <h3
                          className={`text-gray-800 ${
                            milestone.completed ? "font-medium" : ""
                          }`}
                        >
                          {milestone.name}
                        </h3>
                        <div className="flex items-center">
                          {milestone.completed && (
                            <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded mr-2">
                              {milestone.date}
                            </span>
                          )}
                          <button
                            onClick={() => deleteMilestone(index)}
                            className="text-gray-400 hover:text-red-500 transition-colors opacity-0 group-hover:opacity-100"
                          >
                            <FiTrash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </div>

                      {milestone.completed && (
                        <div className="flex justify-end mt-1">
                          <span className="text-xs text-gray-400">
                            Completed {milestone.date}
                          </span>
                        </div>
                      )}
                    </div>
                  </motion.div>
                ))}
              </div>

              <div className="flex items-center space-x-2">
                <input
                  type="text"
                  value={newMilestoneName}
                  onChange={(e) => setNewMilestoneName(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && addMilestone()}
                  className="flex-grow border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"
                  placeholder="Add new milestone..."
                />
                <button
                  onClick={addMilestone}
                  className="flex items-center px-4 py-2 bg-blue-500 text-white text-sm font-medium rounded-lg hover:bg-blue-600 transition-colors"
                >
                  <FiPlus className="mr-1" />
                  Add
                </button>
              </div>
            </motion.div>

            {/* Bugs Section */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.3, delay: 0.3 }}
              className="bg-white shadow rounded-lg p-6 mb-8"
            >
              <h2 className="text-xl font-semibold text-gray-800 mb-4">
                Bugs Related to Project
              </h2>
              {loadingBugs ? (
                <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
              ) : errorBugs ? (
                <p className="text-red-600">{errorBugs}</p>
              ) : (
                <div className="space-y-3">
                  {bugs.length > 0 ? (
                    bugs.map((bug) => (
                      <div
                        key={bug.id}
                        className="flex justify-between items-center border-b py-2"
                      >
                        <div>
                          <h3 className="text-gray-800 font-medium">
                            {bug.title}
                          </h3>
                          <p className="text-gray-600">{bug.description}</p>
                        </div>
                        <div className="flex items-center">
                          <span
                            className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(
                              bug.status
                            )}`}
                          >
                            {bug.status}
                          </span>
                          <span
                            className={`px-2 py-1 text-xs font-medium rounded-full ${getPriorityColor(
                              bug.priority
                            )}`}
                          >
                            {bug.priority}
                          </span>
                          <button
                            onClick={() => deleteBug(bug.id)}
                            className="text-red-600 hover:text-red-900 ml-3"
                          >
                            <FiTrash2 />
                          </button>
                        </div>
                      </div>
                    ))
                  ) : (
                    <p className="text-gray-500">
                      No bugs reported for this project.
                    </p>
                  )}
                </div>
              )}
            </motion.div>
          </div>

          {/* Sidebar */}
          <div>
            {/* Team Members */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.3, delay: 0.4 }}
              className="bg-white shadow rounded-lg p-6 mb-8"
            >
              <h2 className="text-xl font-semibold text-gray-800 mb-4">
                Team Members
              </h2>
              {isEditing ? (
                <div>
                  <textarea
                    value={editedProject.team?.join(', ') || ''}
                    onChange={(e) => setEditedProject({...editedProject, team: e.target.value.split(',').map(t => t.trim())})}
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
                    placeholder="Enter team members separated by commas"
                  />
                </div>
              ) : (
                <div className="space-y-3">
                  {project.team?.map((member, index) => (
                    <div key={index} className="flex items-center">
                      <div className="flex-shrink-0 h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-medium mr-3">
                        {member.charAt(0)}
                      </div>
                      <div>
                        <p className="text-gray-800 font-medium">{member}</p>
                        <p className="text-sm text-gray-500">
                          {project.team.length > 3 && index === 0
                            ? "Project Lead"
                            : "Team Member"}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </motion.div>

            {/* Technologies */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.3, delay: 0.5 }}
              className="bg-white shadow rounded-lg p-6 mb-8"
            >
              <h2 className="text-xl font-semibold text-gray-800 mb-4">
                Technologies
              </h2>
              {isEditing ? (
                <div>
                  <input
                    type="text"
                    value={editedProject.technologies?.join(', ') || ''}
                    onChange={(e) => setEditedProject({...editedProject, technologies: e.target.value.split(',').map(t => t.trim())})}
                    className="w-full border border-gray-300 rounded px-3 py-2 text-sm"
                    placeholder="Enter technologies separated by commas"
                  />
                </div>
              ) : (
                <div className="flex flex-wrap gap-2">
                  {project.technologies?.map((tech, index) => (
                    <span
                      key={index}
                      className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800"
                    >
                      {tech}
                    </span>
                  ))}
                </div>
              )}
            </motion.div>
          </div>
        </div>
      </div>
    </div>
    </DashbordLayout>
  );
};

export default ProjectDetailsPage;
