import axios from "axios";
import { useState, useEffect, useRef } from "react";
import {
  FiPlus,
  FiX,
  FiChevronDown,
  FiCalendar,
  FiAlertCircle,
  FiUser,
  FiGlobe,
  FiChrome,
  FiMonitor,
  FiUpload,
  FiPaperclip,
  FiTrash2,
} from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import Dashboard from "./Dashboard";
import DashbordLayout from "../../components/layout/DashbordLayout";

const BugCreationPage = () => {
  const navigate = useNavigate();
  const fileInputRef = useRef(null);
  const [formData, setFormData] = useState({
    projectId: "",
    title: "",
    description: "",
    assignedUserId: "",
    deadLine: "",
    priority: "MEDIUM",
    environment: "",
    browser: "",
    os: "",
    attachments: [],
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);

  // Sample data
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const environmentOptions = ["Production", "Staging", "Development", "Local"];
  const browserOptions = [
    "Chrome",
    "Firefox",
    "Safari",
    "Edge",
    "Internet Explorer",
    "Other",
  ];
  const osOptions = ["Windows", "macOS", "Linux", "iOS", "Android", "Other"];

  // Fetch projects and users
  useEffect(() => {
    const token = localStorage.getItem("jwt");
    const fetchProjects = async () => {
      const mockProjects = [
        { id: "1", name: "E-commerce Platform" },
        { id: "2", name: "Mobile Banking App" },
        { id: "3", name: "Corporate Website" },
      ];
      const api = axios.create({
        baseURL: "http://localhost:8083",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      try {
        const response = await api.get("/api/projects");
        console.log(response.data);
        const simplified = response.data.map((proj) => ({
          id: proj.id,
          name: proj.title,
        }));

        setProjects(simplified);
        console.log("fetched project data ", simplified);
      } catch (error) {
        console.log("failed to fetch project data ", error);
      }
    };

    const fetchUsers = async () => {
      const mockUsers = [
        { id: "101", name: "John Doe", role: "Developer" },
        { id: "102", name: "Jane Smith", role: "QA Engineer" },
        { id: "103", name: "Bob Johnson", role: "Project Manager" },
      ];
      const api = axios.create({
        baseURL: "http://localhost:8081",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      try {
        const response = await api.get("/api/user");
        console.log("fetched user data unfiltered", response.data);
        const simplified = response.data.map((user) => ({
          id: user.id,
          name: user.name,
        }));

        setUsers(simplified);

        console.log("fetched user data ", simplified);
      } catch (error) {
        console.log("failed to fetch data", error);
      }
    };

    fetchProjects();
    fetchUsers();
  }, []);

  const uploadFileToService = async (file) => {
    setIsUploading(true);
    setUploadProgress(0);

    // Simulate file upload progress
    const interval = setInterval(() => {
      setUploadProgress((prev) => {
        if (prev >= 100) {
          clearInterval(interval);
          return 100;
        }
        return prev + 10;
      });
    }, 300);

    // // Simulating API call delay
    // return new Promise((resolve) => {
    //   setTimeout(() => {
    //     clearInterval(interval);
    //     setIsUploading(false);
    //     setUploadProgress(0);
    //     resolve({
    //       url: `https://localhost:8085/api/attachments/upload${
    //         file.name
    //       }_${Date.now()}`,
    //       name: file.name,
    //       type: file.type,
    //     });
    //   }, 2000);
    // });
  };

  const handleFileChange = async (e) => {
    const files = e.target.files;
    if (!files.length) return;

    const token = localStorage.getItem("jwt");

    const api = axios.create({
      baseURL: "http://localhost:8085",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    try {
      const uploadedFiles = [];
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const formData = new FormData();
        formData.append("file", file);
        formData.append("fileName", file.name);
        const response = await api.post("api/attachments/upload", formData);

        uploadedFiles.push(response.data);

        console.log("upload file ", response.data);
      }

      setFormData((prev) => ({
        ...prev,
        attachments: [...prev.attachments, ...uploadedFiles],
      }));
    } catch (error) {
      console.error("Error uploading file:", error);
      alert("Failed to upload file");
    }
  };

  const removeAttachment = (index) => {
    setFormData((prev) => ({
      ...prev,
      attachments: prev.attachments.filter((_, i) => i !== index),
    }));
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: null,
      });
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.projectId) newErrors.projectId = "Project is required";
    if (!formData.title.trim()) newErrors.title = "Title is required";
    if (!formData.description.trim())
      newErrors.description = "Description is required";
    if (!formData.assignedUserId)
      newErrors.assignedUserId = "Assignee is required";
    if (!formData.deadLine) newErrors.deadLine = "Deadline is required";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      setIsSubmitting(true);

      try {
        const bugReport = {
          projectId: formData.projectId,
          title: formData.title,
          description: formData.description,
          assignedUserId: formData.assignedUserId,
          deadLine: formData.deadLine,
          priority: formData.priority,
          environment: formData.environment,
          browser: formData.browser,
          os: formData.os,
          attachments: formData.attachments.map((file) => file.fileUrl), // Only send URLs
        };

        console.log("Submitting bug report:", bugReport);

        // In a real app, you would call your API here:
        // const response = await fetch('/api/bugs', {
        //   method: 'POST',
        //   headers: { 'Content-Type': 'application/json' },
        //   body: JSON.stringify(bugReport)
        // });
        const token = localStorage.getItem("jwt");
        const api = axios.create({
          baseURL: "http://localhost:8080",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        const response = await api.post("/api/bugs/add",bugReport);

        // Simulate API delay
        await new Promise((resolve) => setTimeout(resolve, 1000));

        navigate("/admin/dashboard"); // Redirect to bugs page
      } catch (error) {
        console.error("Error submitting bug:", error);
        alert("Failed to submit bug report");
      } finally {
        setIsSubmitting(false);
      }
    }
  };

  return (
    <DashbordLayout activeMenu="Create Bug">
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-2xl mx-auto bg-white rounded-xl shadow-md overflow-hidden">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-2xl font-bold text-gray-800">Create New Bug</h1>
           
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Project Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Project
              </label>
              <select
                name="projectId"
                value={formData.projectId}
                onChange={handleChange}
                className={`w-full px-3 py-2 border rounded-lg ${
                  errors.projectId ? "border-red-500" : "border-gray-300"
                }`}
              >
                <option value="">Select a project</option>
                {projects.map((project) => (
                  <option key={project.id} value={project.id}>
                    {project.name}
                  </option>
                ))}
              </select>
              {errors.projectId && (
                <p className="mt-1 text-sm text-red-600">{errors.projectId}</p>
              )}
            </div>

            {/* Title */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Title
              </label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                className={`w-full px-3 py-2 border rounded-lg ${
                  errors.title ? "border-red-500" : "border-gray-300"
                }`}
                placeholder="Brief description of the bug"
              />
              {errors.title && (
                <p className="mt-1 text-sm text-red-600">{errors.title}</p>
              )}
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows={4}
                className={`w-full px-3 py-2 border rounded-lg ${
                  errors.description ? "border-red-500" : "border-gray-300"
                }`}
                placeholder="Detailed description of the bug..."
              />
              {errors.description && (
                <p className="mt-1 text-sm text-red-600">
                  {errors.description}
                </p>
              )}
            </div>

            {/* Environment */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                <FiGlobe className="mr-2" />
                Environment
              </label>
              <select
                name="environment"
                value={formData.environment}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              >
                <option value="">Select environment</option>
                {environmentOptions.map((env) => (
                  <option key={env} value={env}>
                    {env}
                  </option>
                ))}
              </select>
            </div>

            {/* Browser */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                <FiChrome className="mr-2" />
                Browser
              </label>
              <select
                name="browser"
                value={formData.browser}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              >
                <option value="">Select browser</option>
                {browserOptions.map((browser) => (
                  <option key={browser} value={browser}>
                    {browser}
                  </option>
                ))}
              </select>
            </div>

            {/* Operating System */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                <FiMonitor className="mr-2" />
                Operating System
              </label>
              <select
                name="os"
                value={formData.os}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg"
              >
                <option value="">Select OS</option>
                {osOptions.map((os) => (
                  <option key={os} value={os}>
                    {os}
                  </option>
                ))}
              </select>
            </div>

            {/* Assignee */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                <FiUser className="mr-2" />
                Assigned To
              </label>
              <select
                name="assignedUserId"
                value={formData.assignedUserId}
                onChange={handleChange}
                className={`w-full px-3 py-2 border rounded-lg ${
                  errors.assignedUserId ? "border-red-500" : "border-gray-300"
                }`}
              >
                <option value="">Select assignee</option>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.name}
                  </option>
                ))}
              </select>
              {errors.assignedUserId && (
                <p className="mt-1 text-sm text-red-600">
                  {errors.assignedUserId}
                </p>
              )}
            </div>

            {/* Deadline */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                <FiCalendar className="mr-2" />
                Deadline
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                  <FiCalendar className="text-gray-400" />
                </div>
                <input
                  type="date"
                  name="deadLine"
                  value={formData.deadLine}
                  onChange={handleChange}
                  className={`w-full pl-10 pr-3 py-2 border rounded-lg ${
                    errors.deadLine ? "border-red-500" : "border-gray-300"
                  }`}
                  min={new Date().toISOString().split("T")[0]}
                />
              </div>
              {errors.deadLine && (
                <p className="mt-1 text-sm text-red-600">{errors.deadLine}</p>
              )}
            </div>

            {/* File Upload Section */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2 flex items-center">
                <FiPaperclip className="mr-2" />
                Attachments
              </label>
              <input
                type="file"
                ref={fileInputRef}
                onChange={handleFileChange}
                multiple
                accept="image/*,.pdf,.doc,.docx,.txt,.log"
                className="hidden"
              />
              <button
                type="button"
                onClick={() => fileInputRef.current.click()}
                className="w-full flex items-center justify-center px-4 py-2 border border-dashed rounded-lg mb-3 hover:bg-gray-50 cursor-pointer"
              >
                <FiUpload className="mr-2" />
                Select Files
              </button>
              <div className="space-y-2">
                {formData.attachments.map((file, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between p-2 bg-gray-50 rounded-lg"
                  >
                    <div className="flex items-center truncate">
                      <FiPaperclip className="mr-2 text-gray-400 flex-shrink-0" />
                      <span className="truncate">{file.fileName}</span>
                    </div>
                    <button
                      type="button"
                      onClick={() => removeAttachment(index)}
                      className="text-red-500 hover:text-red-700 ml-2"
                    >
                      <FiTrash2 />
                    </button>
                  </div>
                ))}
              </div>
            </div>

            {/* Priority */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Priority
              </label>
              <div className="grid grid-cols-3 gap-3">
                <label
                  className={`flex items-center justify-center p-3 border rounded-lg cursor-pointer ${
                    formData.priority === "HIGH"
                      ? "bg-red-100 border-red-500"
                      : "border-gray-300"
                  }`}
                >
                  <div className="flex items-center">
                    <FiAlertCircle
                      className={`mr-2 ${
                        formData.priority === "HIGH"
                          ? "text-red-600"
                          : "text-gray-400"
                      }`}
                    />
                    <input
                      type="radio"
                      name="priority"
                      value="HIGH"
                      checked={formData.priority === "HIGH"}
                      onChange={handleChange}
                      className="hidden"
                    />
                    <span>High</span>
                  </div>
                </label>
                <label
                  className={`flex items-center justify-center p-3 border rounded-lg cursor-pointer ${
                    formData.priority === "MEDIUM"
                      ? "bg-yellow-100 border-yellow-500"
                      : "border-gray-300"
                  }`}
                >
                  <div className="flex items-center">
                    <FiAlertCircle
                      className={`mr-2 ${
                        formData.priority === "MEDIUM"
                          ? "text-yellow-600"
                          : "text-gray-400"
                      }`}
                    />
                    <input
                      type="radio"
                      name="priority"
                      value="MEDIUM"
                      checked={formData.priority === "MEDIUM"}
                      onChange={handleChange}
                      className="hidden"
                    />
                    <span>Medium</span>
                  </div>
                </label>
                <label
                  className={`flex items-center justify-center p-3 border rounded-lg cursor-pointer ${
                    formData.priority === "LOW"
                      ? "bg-green-100 border-green-500"
                      : "border-gray-300"
                  }`}
                >
                  <div className="flex items-center">
                    <FiAlertCircle
                      className={`mr-2 ${
                        formData.priority === "LOW"
                          ? "text-green-600"
                          : "text-gray-400"
                      }`}
                    />
                    <input
                      type="radio"
                      name="priority"
                      value="LOW"
                      checked={formData.priority === "LOW"}
                      onChange={handleChange}
                      className="hidden"
                    />
                    <span>Low</span>
                  </div>
                </label>
              </div>
            </div>

            {/* Submit Button */}
            <div className="pt-6">
              <button
                type="submit"
                disabled={isSubmitting}
                className={`w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 ${
                  isSubmitting ? "opacity-70 cursor-not-allowed" : ""
                }`}
              >
                {isSubmitting ? (
                  <>
                    <svg
                      className="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                    >
                      <circle
                        className="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        strokeWidth="4"
                      ></circle>
                      <path
                        className="opacity-75"
                        fill="currentColor"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                      ></path>
                    </svg>
                    Creating Bug...
                  </>
                ) : (
                  <>
                    <FiPlus className="-ml-1 mr-2 h-4 w-4" />
                    Create Bug
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    </DashbordLayout>
  );
};

export default BugCreationPage;
