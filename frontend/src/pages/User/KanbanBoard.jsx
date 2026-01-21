import React, { useState, useEffect } from "react";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import {
  FiChevronDown,
  FiChevronRight,
  FiPlus,
  FiEdit2,
  FiTrash2,
} from "react-icons/fi";
import DashbordLayout from "../../components/layout/DashbordLayout";

const statuses = [
  "Backlog",
  "To Do",
  "In Progress",
  "Peer Review",
  "Testing / QA",
  "Blocked",
  "Done",
];

const priorityColor = {
  high: "bg-red-100 text-red-800",
  medium: "bg-yellow-100 text-yellow-800",
  low: "bg-green-100 text-green-800",
};

const initialTasks = [
  {
    id: "1",
    title: "Update authentication flow",
    description: "Implement OAuth2 login with refresh tokens",
    status: "Backlog",
    priority: "high",
  },
  {
    id: "2",
    title: "Database optimization",
    description: "Index optimization for query performance",
    status: "Backlog",
    priority: "medium",
  },
  {
    id: "3",
    title: "Dashboard redesign",
    description: "Implement new UI components",
    status: "To Do",
    priority: "high",
  },
  {
    id: "4",
    title: "Payment gateway",
    description: "Stripe integration with 3D secure",
    status: "In Progress",
    priority: "high",
  },
  {
    id: "5",
    title: "API documentation",
    description: "Generate Swagger docs for all endpoints",
    status: "Peer Review",
    priority: "medium",
  },
];

const Card = ({ task, index, onUpdate, onDelete }) => {
  const [editMode, setEditMode] = useState(false);
  const [editableTask, setEditableTask] = useState(task);

  const handleSave = () => {
    onUpdate(editableTask);
    setEditMode(false);
  };

  return (
    <Draggable draggableId={task.id} index={index}>
      {(provided) => (
        <div
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          className="bg-white rounded-lg shadow-sm p-3 mb-3 border border-gray-100 hover:border-blue-200 transition-colors duration-150 min-h-[150px]"
        >
          {editMode ? (
            <div className="space-y-2">
              <input
                value={editableTask.title}
                onChange={(e) =>
                  setEditableTask({ ...editableTask, title: e.target.value })
                }
                className="w-full text-sm border border-gray-300 rounded px-2 py-1 focus:ring-2 focus:ring-blue-100 focus:outline-none"
                autoFocus
              />
              <textarea
                value={editableTask.description}
                onChange={(e) =>
                  setEditableTask({
                    ...editableTask,
                    description: e.target.value,
                  })
                }
                className="w-full text-sm border border-gray-300 rounded px-2 py-1 h-20 focus:ring-2 focus:ring-blue-100 focus:outline-none"
              />

              {/* Priority selector */}
              <select
                value={editableTask.priority}
                onChange={(e) =>
                  setEditableTask({ ...editableTask, priority: e.target.value })
                }
                className="w-full text-sm border border-gray-300 rounded px-2 py-1 focus:ring-2 focus:ring-blue-100 focus:outline-none"
              >
                <option value="high">High</option>
                <option value="medium">Medium</option>
                <option value="low">Low</option>
              </select>

              <div className="flex justify-end space-x-2">
                <button
                  onClick={handleSave}
                  className="px-3 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
                >
                  Save
                </button>
                <button
                  onClick={() => setEditMode(false)}
                  className="px-3 py-1 text-sm bg-gray-100 text-gray-700 rounded hover:bg-gray-200 transition-colors"
                >
                  Cancel
                </button>
              </div>
            </div>
          ) : (
            <div className="space-y-2">
              <h3 className="font-medium text-gray-800 text-sm">{task.title}</h3>
              <p className="text-gray-600 text-xs">{task.description}</p>
              <div className="flex items-center justify-between text-sm">
                <button
                  onClick={() => setEditMode(true)}
                  className="text-blue-500 hover:text-blue-700 text-xs flex items-center"
                >
                  <FiEdit2 className="mr-1" size={12} />
                  Edit
                </button>
                <button
                  onClick={() => onDelete(task.id)}
                  className="text-red-500 hover:text-red-700 text-xs flex items-center"
                >
                  <FiTrash2 className="mr-1" size={12} />
                  Delete
                </button>
                <span
                  className={`text-xs px-2 py-0.5 rounded-full ${
                    priorityColor[task.priority]
                  }`}
                >
                  {task.priority}
                </span>
              </div>
            </div>
          )}
        </div>
      )}
    </Draggable>
  );
};

const Column = ({
  title,
  tasks,
  onAdd,
  onUpdate,
  onDelete,
  collapsed,
  onToggleCollapse,
}) => {
  return (
    <div
      className={`relative flex flex-col transition-all duration-300 ${
        collapsed ? "w-[48px] min-h-[220px] items-center" : "w-64"
      }`}
    >
      <div
        className={`bg-white rounded-lg shadow-sm p-2 flex items-center justify-between z-10 ${
          collapsed ? "flex-col h-16 justify-center" : ""
        } sticky top-0`}
      >
        <div
          className={`flex items-center ${collapsed ? "flex-col" : "w-full"}`}
        >
          <button
            onClick={onToggleCollapse}
            className={`text-gray-500 hover:text-gray-700 transition-colors ${
              collapsed ? "mb-1" : "mr-2"
            }`}
            title={collapsed ? "Expand column" : "Collapse column"}
          >
            {collapsed ? <FiChevronRight size={16} /> : <FiChevronDown size={16} />}
          </button>

          {!collapsed && (
            <>
              <h2 className="font-semibold text-gray-700 text-sm truncate">
                {title}
              </h2>
              <span className="ml-auto text-xs bg-blue-100 text-blue-800 rounded-full px-2 py-0.5">
                {tasks.length}
              </span>
            </>
          )}
        </div>
      </div>

      {!collapsed && (
        <div className="mt-2 flex-1">
          <Droppable droppableId={title}>
            {(provided) => (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                className="h-full overflow-y-auto px-1"
              >
                {tasks.map((task, index) => (
                  <Card
                    key={task.id}
                    task={task}
                    index={index}
                    onUpdate={onUpdate}
                    onDelete={onDelete}
                  />
                ))}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
          <div className="sticky bottom-0 bg-white pt-2">
            <button
              onClick={onAdd}
              className="mt-2 w-full text-xs text-gray-600 border border-dashed border-gray-300 rounded p-2 flex items-center justify-center hover:bg-blue-50 hover:text-blue-600 hover:border-blue-200 transition-colors"
            >
              <FiPlus className="mr-1" size={14} />
              Add task
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

const KanbanBoard = () => {
  // Get userId however you store it (localStorage, context, etc)
  const userId = localStorage.getItem("userId") || "defaultUser";
  const localStorageKey = `tasks_${userId}`;

  const [tasks, setTasks] = useState(() => {
    const savedTasks = localStorage.getItem(localStorageKey);
    return savedTasks ? JSON.parse(savedTasks) : initialTasks;
  });

  const [collapsedColumns, setCollapsedColumns] = useState({});

  useEffect(() => {
    localStorage.setItem(localStorageKey, JSON.stringify(tasks));
  }, [tasks, localStorageKey]);

  const handleDeleteCard = (taskId) => {
    setTasks((prev) => prev.filter((t) => t.id !== taskId));
  };

  const handleAddCard = (status) => {
    const newTask = {
      id: Date.now().toString(),
      title: "New Task",
      description: "Click edit to add details...",
      status,
      priority: "low",
    };
    setTasks((prev) => [...prev, newTask]);
  };

  const handleUpdateTask = (updatedTask) => {
    setTasks((prev) => prev.map((t) => (t.id === updatedTask.id ? updatedTask : t)));
  };

  const toggleColumnCollapse = (column) => {
    setCollapsedColumns((prev) => ({
      ...prev,
      [column]: !prev[column],
    }));
  };

  const onDragEnd = (result) => {
    const { destination, draggableId } = result;
    if (!destination) return;

    setTasks((prev) =>
      prev.map((task) =>
        task.id === draggableId ? { ...task, status: destination.droppableId } : task
      )
    );
  };

  return (
    <DashbordLayout>
      <div className="min-h-screen bg-gray-50 p-6">
        <div className="max-w-7xl mx-auto">
          <h1 className="text-2xl font-bold text-gray-800 mb-6">Kanban Board</h1>

          <div className="bg-white rounded-xl shadow p-4 mb-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="font-medium text-gray-700">Active Sprint</h2>
              <div className="flex space-x-2">
                <button className="text-xs bg-blue-50 text-blue-600 px-3 py-1 rounded hover:bg-blue-100">
                  Filter
                </button>
                <button className="text-xs bg-gray-50 text-gray-600 px-3 py-1 rounded hover:bg-gray-100">
                  View
                </button>
              </div>
            </div>

            <DragDropContext onDragEnd={onDragEnd}>
              <div className="flex gap-4 overflow-x-auto pb-2 min-h-[65vh]">
                {statuses.map((status) => (
                  <Column
                    key={status}
                    title={status}
                    tasks={tasks.filter((task) => task.status === status)}
                    onAdd={() => handleAddCard(status)}
                    onUpdate={handleUpdateTask}
                    onDelete={handleDeleteCard}
                    collapsed={collapsedColumns[status] || false}
                    onToggleCollapse={() => toggleColumnCollapse(status)}
                  />
                ))}
              </div>
            </DragDropContext>
          </div>
        </div>
      </div>
    </DashbordLayout>
  );
};

export default KanbanBoard;
